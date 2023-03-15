package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.checkers.CheckerRegistry
import ejektaflex.bountiful.data.bounty.enums.BountyNBT
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.ext.edit
import ejektaflex.bountiful.ext.sendTranslation
import ejektaflex.bountiful.ext.toData
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.*


class ItemBounty : Item(
        Properties().stacksTo(1)
) {

    /**
     * Thrown when bounty NBT data could not be created
     */
    class BountyCreationException(err: String = "Bounty could not be created!") : Exception(err)


    override fun getName(stack: ItemStack): Component {

        return if (BountyData.isValidBounty(stack)) {
            val bd = stack.toData(::BountyData)
            Component.translatable("bountiful.rarity.${bd.rarityEnum.name}").apply {

                append(Component.literal(" "))

                append(super.getName(stack))

                //  Only runs on physical client
                if (FMLEnvironment.dist == Dist.CLIENT) {
                    Minecraft.getInstance().level?.let { level ->
                        append(
                                Component.literal(
                                        " §f(${bd.remainingTime(level)}§f)"
                                )
                        )
                    }
                }

            }
        } else {
            super.getName(stack)
        }

    }

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        if (context.player == null) {
            return InteractionResult.FAIL
        }
        if (!BountifulConfig.SERVER.cashInAtBountyBoard.get()) {
            cashIn(context.player!!, context.hand)
        } else {
            context.player?.sendTranslation("bountiful.bounty.turnin")
        }
        return super.onItemUseFirst(stack, context)
    }


    @OnlyIn(Dist.CLIENT)
    override fun appendHoverText(stack: ItemStack, worldIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        if (stack.hasTag()) {
            val bounty = stack.toData(::BountyData)
            val bountyTipInfo = bounty.tooltipInfo(worldIn!!, Minecraft.getInstance().player?.isCrouching == true)
            for (line in bountyTipInfo) {
                tooltip.add(line)
            }
        }
    }

    override fun getRarity(stack: ItemStack): Rarity {
        return if (stack.hasTag() && BountyNBT.Rarity.key in stack.tag!!) {
            BountyRarity.getRarityFromInt(stack.tag!!.getInt(BountyNBT.Rarity.key)).itemRarity
        } else {
            super.getRarity(stack)
        }
    }

    fun ensureTimerStarted(stack: ItemStack, levelIn: Level) {
        if (stack.item is ItemBounty && stack.hasTag() && BountyNBT.BountyStamp.key !in stack.tag!!) {
            stack.tag!!.putLong(BountyNBT.BountyStamp.key, levelIn.gameTime)
        }
    }

    override fun inventoryTick(stack: ItemStack, levelIn: Level, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (!levelIn.isClientSide) {
            if (levelIn.gameTime % BountyData.bountyTickFreq == 3L) {
                ensureTimerStarted(stack, levelIn)
            }
        }
    }


    fun ensureBounty(stack: ItemStack, levelIn: Level, decrees: List<Decree>, rarity: BountyRarity) {

        val data = try {
            BountyData.create(rarity, decrees)
        } catch (e: BountyCreationException) {
            return
        }

        if (stack.item is ItemBounty) {
            if (!stack.hasTag()) {
                stack.tag = data.serializeNBT().apply {
                    this.remove(BountyNBT.BountyStamp.key)
                    this.putLong(BountyNBT.BoardStamp.key, levelIn.gameTime)
                }
            }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty, so you cannot generate bounty data for it!")
        }

    }

    // Used to cash in the bounty for a reward
    fun cashIn(player: Player, hand: InteractionHand): Boolean {
        val bountyItem = player.getItemInHand(hand)
        if (!bountyItem.hasTag()) {
            ensureBounty(player.getItemInHand(hand), player.level, DecreeRegistry.content, calcRarity())
            return false
        }

        val bounty = bountyItem.toData(::BountyData)

        if (bounty.hasExpired(player.level)) {
            player.sendTranslation("bountiful.bounty.expired")
            return false
        }

        val succ = CheckerRegistry.tryCashIn(player, bounty)

        if (succ) {
            bountyItem.shrink(bountyItem.maxStackSize)
        } else {
            player.sendTranslation("bountiful.tooltip.requirements")
        }

        return false
    }

    // Don't flail arms randomly on NBT update
    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return slotChanged
    }

    companion object {

        private val rand = Random()

        fun calcRarity(): BountyRarity {
            var level = 0
            val chance = BountifulConfig.SERVER.rarityChance.get()
            for (i in 0 until 3) {
                if (rand.nextFloat() < chance) {
                    level += 1
                } else {
                    break
                }
            }
            return BountyRarity.getRarityFromInt(level)
        }

        fun create(levelIn: Level, decrees: List<Decree>): ItemStack {
            return ItemStack(BountifulContent.BOUNTY).apply {
                edit<ItemBounty> {
                    ensureBounty(it, levelIn, decrees, calcRarity())
                }
            }
        }

    }

}