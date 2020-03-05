package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.ext.sendTranslation
import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.enums.BountyNBT
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.data.bounty.checkers.CheckerRegistry
import ejektaflex.bountiful.registry.DecreeRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Rarity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.registries.IForgeRegistryEntry


class ItemBounty() : Item(
        Item.Properties().maxStackSize(1).group(BountifulContent.BountifulGroup)
), IForgeRegistryEntry<Item> {

    /**
     * Thrown when bounty NBT data could not be created
     */
    class BountyCreationException(err: String = "Bounty could not be created!") : Exception(err)

    init {
        addPropertyOverride(ResourceLocation("bountiful", "rarity")) { stack, world, entity ->
            val bd = BountyData.safeData(stack)
            if (bd != null) {
                bd.rarity * 0.1f
            } else {
                0.0f
            }
        }
    }

    override fun getTranslationKey() = "bountiful.bounty"

    override fun getDisplayName(stack: ItemStack): ITextComponent {

        return if (BountyData.isValidBounty(stack)) {
            val bd = getBountyData(stack)
            TranslationTextComponent("bountiful.rarity.${bd.rarityEnum.name}").apply {

                appendSibling(StringTextComponent(" "))

                appendSibling(super.getDisplayName(stack))

                Minecraft.getInstance().world?.let { wrld ->
                    appendSibling(
                            StringTextComponent(
                                    " §f(${bd.remainingTime(wrld)}§f)"
                            )
                    )
                }

            }
        } else {
            super.getDisplayName(stack)
        }

    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {

        if (worldIn.isRemote) {
            return super.onItemRightClick(worldIn, playerIn, handIn)
        }

        if (!BountifulConfig.SERVER.cashInAtBountyBoard.get()) {
            cashIn(playerIn, handIn)
        } else {
            playerIn.sendMessage(TranslationTextComponent("bountiful.bounty.turnin"))
        }

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        if (stack.hasTag()) {
            val bounty = BountyData().apply { deserializeNBT(stack.tag!!) }
            // TODO Reimplement advanced bounty tooltips
            //val bountyTipInfo = bounty.tooltipInfo(worldIn!!, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            val bountyTipInfo = bounty.tooltipInfo(worldIn!!, false)
            for (line in bountyTipInfo) {
                tooltip.add(line)
            }
        }
    }

    fun getBountyData(stack: ItemStack): BountyData {
        if (stack.hasTag() && stack.item is ItemBounty) {
            return BountyData().apply { deserializeNBT(stack.tag!!) }
        } else {
            throw Exception("${stack.item.registryName} is not an ItemBounty or has no NBT data!")
        }
    }

    override fun getRarity(stack: ItemStack): Rarity {
        return if (stack.hasTag() && BountyNBT.Rarity.key in stack.tag!!) {
            BountyRarity.getRarityFromInt(stack.tag!!.getInt(BountyNBT.Rarity.key)).itemRarity
        } else {
            super.getRarity(stack)
        }
    }

    private fun tickNumber(stack: ItemStack, amount: Int, key: String): Boolean {
        if (stack.hasTag() && key in stack.tag!!) {
            var time = stack.tag!!.getInt(key)
            if (time > 0) {
                time -= amount
            }
            if (time < 0) {
                time = 0
            }
            stack.tag!!.putInt(key, time)
            return (time <= 0)
        }
        return true
    }

    fun tryExpireBountyTime(stack: ItemStack) {
        if (stack.hasTag() && BountyNBT.BountyTime.key in stack.tag!!) {
            stack.tag!!.putInt(BountyNBT.BountyTime.key, 0)
        }
    }

    fun tickBoardTime(stack: ItemStack): Boolean {
        return tickNumber(stack, BountyData.boardTickFreq.toInt(), BountyNBT.BoardStamp.key)
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
            BountyCreator.create(rarity, decrees)
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
            ensureBounty(player.getHeldItem(hand), player.world, DecreeRegistry.content, BountyCreator.calcRarity())
            return false
        }

        val bounty = BountyData().apply { deserializeNBT(bountyItem.tag!!) }

        if (bounty.hasExpired(player.world)) {
            player.sendTranslation("bountiful.bounty.expired")
            return false
        }

        val succ = CheckerRegistry.tryCashIn(player, bounty)

        if (succ) {
            bountyItem.shrink(bountyItem.maxStackSize)
        }

        return false

        /*

            // Increment stats

            // TODO Reimplement Scoreboard Stats
            //player.addStat(BountifulStats.bountiesCompleted)
            //player.addStat(bountyRarity.stat)

            // Give XP
            player.giveExperiencePoints(bountyRarity.xp)

            true
        }

         */


    }

    // Don't flail arms randomly on NBT update
    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return slotChanged
    }

    companion object {

        fun edit(stack: ItemStack, operation: ItemStack.(it: BountyData) -> Unit) {
            if (stack.item is ItemBounty) {
                val data = BountyData().apply { deserializeNBT(stack.tag!!) }
                operation(stack, data)
            } else {
                throw Exception("${stack.displayName} is not an ItemBounty, so you cannot edit bounty data for it!")
            }
        }

    }





}