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
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.world.item.ItemStack
import net.minecraft.item.Rarity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.IForgeRegistryEntry
import java.util.*


class ItemBounty : Item(
        Properties().maxStackSize(1).group(ItemGroup.MISC)
), IForgeRegistryEntry<Item> {

    /**
     * Thrown when bounty NBT data could not be created
     */
    class BountyCreationException(err: String = "Bounty could not be created!") : Exception(err)

    override fun getTranslationKey() = "bountiful.bounty"

    override fun getDisplayName(stack: ItemStack): ITextComponent {

        return if (BountyData.isValidBounty(stack)) {
            val bd = stack.toData(::BountyData)
            TranslationTextComponent("bountiful.rarity.${bd.rarityEnum.name}").apply {

                append(StringTextComponent(" "))

                append(super.getDisplayName(stack))

                //  Only runs on physical client
                if (FMLEnvironment.dist == Dist.CLIENT) {
                    Minecraft.getInstance().world?.let { world ->
                        append(
                                StringTextComponent(
                                        " §f(${bd.remainingTime(world)}§f)"
                                )
                        )
                    }
                }

            }
        } else {
            super.getDisplayName(stack)
        }

    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        if (worldIn.isRemote()) {
            return super.onItemRightClick(worldIn, playerIn, handIn)
        }

        if (!BountifulConfig.SERVER.cashInAtBountyBoard.get()) {
            cashIn(playerIn, handIn)
        } else {
            playerIn.sendTranslation("bountiful.bounty.turnin")
        }

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun onUse(worldIn: World, livingEntityIn: LivingEntity, stack: ItemStack, count: Int) {
        if (worldIn.isRemote) {
            return super.onUse(worldIn, livingEntityIn, stack, count)
        }

        if (!BountifulConfig.SERVER.cashInAtBountyBoard.get()) {
            //cashIn(playerIn, handIn)
        } else {
            //playerIn.sendTranslation("bountiful.bounty.turnin")
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        if (stack.hasTag()) {
            val bounty = stack.toData(::BountyData)
            val bountyTipInfo = bounty.tooltipInfo(worldIn!!, Minecraft.getInstance().player?.isSneaking == true)
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

    fun ensureTimerStarted(stack: ItemStack, worldIn: World) {
        if (stack.item is ItemBounty && stack.hasTag() && BountyNBT.BountyStamp.key !in stack.tag!!) {
            stack.tag!!.putLong(BountyNBT.BountyStamp.key, worldIn.gameTime)
        }
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (!worldIn.isRemote) {
            if (worldIn.gameTime % BountyData.bountyTickFreq == 3L) {
                ensureTimerStarted(stack, worldIn)
            }
        }
    }


    fun ensureBounty(stack: ItemStack, worldIn: World, decrees: List<Decree>, rarity: BountyRarity) {

        val data = try {
            BountyData.create(rarity, decrees)
        } catch (e: BountyCreationException) {
            return
        }

        if (stack.item is ItemBounty) {
            if (!stack.hasTag()) {
                stack.tag = data.serializeNBT().apply {
                    this.remove(BountyNBT.BountyStamp.key)
                    this.putLong(BountyNBT.BoardStamp.key, worldIn.gameTime)
                }
            }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty, so you cannot generate bounty data for it!")
        }

    }

    // Used to cash in the bounty for a reward
    fun cashIn(player: PlayerEntity, hand: Hand): Boolean {
        val bountyItem = player.getHeldItem(hand)
        if (!bountyItem.hasTag()) {
            ensureBounty(player.getHeldItem(hand), player.world, DecreeRegistry.content, calcRarity())
            return false
        }

        val bounty = bountyItem.toData(::BountyData)

        if (bounty.hasExpired(player.world)) {
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

        fun create(world: World, decrees: List<Decree>): ItemStack {
            return ItemStack(BountifulContent.BOUNTY).apply {
                edit<ItemBounty> {
                    ensureBounty(it, world, decrees, calcRarity())
                }
            }
        }

    }

}