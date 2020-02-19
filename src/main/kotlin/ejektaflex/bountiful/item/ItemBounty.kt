package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.BountifulAPI
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.compat.FacadeGameStages
import ejektaflex.bountiful.logic.BountyChecker
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.data.BountyNBT
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.item.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Rarity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn


class ItemBounty(builder: Item.Properties) : Item(builder), IItemBounty {

    override fun getTranslationKey() = "bountiful.bounty"


    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        if (stack.hasTag()) {
            val bounty = BountyData().apply { deserializeNBT(stack.tag!!) }
            // TODO Reimplement advanced bounty tooltips
            //val bountyTipInfo = bounty.tooltipInfo(worldIn!!, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            val bountyTipInfo = bounty.tooltipInfo(worldIn!!, false)
            for (line in bountyTipInfo) {
                tooltip.add(StringTextComponent(line))
            }
        }
    }

    override fun getBountyData(stack: ItemStack): BountyData {
        if (stack.hasTag() && stack.item is ItemBounty) {
            return BountyData().apply { deserializeNBT(stack.tag!!) }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty or has no NBT data!")
        }
    }

    override fun getRarity(stack: ItemStack): Rarity {
        return if (stack.hasTag() && BountyNBT.Rarity.key in stack.tag!!) {
            EnumBountyRarity.getRarityFromInt(stack.tag!!.getInt(BountyNBT.Rarity.key)).itemRarity
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

    override fun tryExpireBountyTime(stack: ItemStack) {
        if (stack.hasTag() && BountyNBT.BountyTime.key in stack.tag!!) {
            stack.tag!!.putInt(BountyNBT.BountyTime.key, 0)
        }
    }

    override fun tickBoardTime(stack: ItemStack): Boolean {
        return tickNumber(stack, BountyData.boardTickFreq.toInt(), BountyNBT.BoardStamp.key)
    }

    // TODO Reimplement Bounty ItemStack display name
    /*
    override fun getItemStackDisplayName(stack: ItemStack): String {
        return super.getItemStackDisplayName(stack) + if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.Rarity.key)) {
            val rarity = EnumBountyRarity.getRarityFromInt(stack.tagCompound!!.getInteger(BountyNBT.Rarity.key))
            val localizedRarity = I18n.format("bountiful.rarity.${rarity.name}")
             " ($localizedRarity)"
        } else {
            ""
        }
    }
    */

    fun ensureTimerStarted(stack: ItemStack, worldIn: World) {
        if (stack.item is ItemBounty && stack.hasTag() && BountyNBT.BountyStamp.key !in stack.tag!!) {
            stack.tag!!.putLong(BountyNBT.BountyStamp.key, worldIn.gameTime)
        }
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (worldIn.gameTime % BountyData.bountyTickFreq == 1L) {
            ensureTimerStarted(stack, worldIn)
        }
    }

    class BountyCreationException(err: String = "Bounty could not be created!") : Exception(err)

    override fun ensureBounty(stack: ItemStack, worldIn: World, rarity: EnumBountyRarity?) {

        val data = try {
            BountifulAPI.createBountyData(worldIn, rarity)
        } catch (e: BountyCreationException) {
            return
        }
        if (stack.item is ItemBounty) {
            if (!stack.hasTag()) {
                if (data != null) {
                    stack.tag = data.serializeNBT().apply {
                        this.remove(BountyNBT.BountyStamp.key)
                        this.putLong(BountyNBT.BoardStamp.key, worldIn.gameTime)
                    }
                }
            }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty, so you cannot generate bounty data for it!")
        }

    }

    // Used to cash in the bounty for a reward
    fun cashIn(player: PlayerEntity, hand: Hand, atBoard: Boolean = false): Boolean {
        val bountyItem = player.getHeldItem(hand)
        if (!bountyItem.hasTag()) {
            ensureBounty(player.getHeldItem(hand), player.world)
            return false
        }

        val inv = player.inventory.mainInventory
        val bounty = BountyData().apply { deserializeNBT(bountyItem.tag!!) }

        // Gate behind gamestages
        if (BountifulMod.config.isRunningGameStages && FacadeGameStages.stagesStillNeededFor(player, bounty).isNotEmpty()) {
            //player.sendTranslation("bountiful.tooltip.requirements")

            return false
        }

        if (bounty.hasExpired(player.world)) {
            //player.sendTranslation("bountiful.bounty.expired")
            return false
        }

        // Returns prerequisite stacks needed to reduce for reward, or null if they don't have the prereqs
        val invItemsAffected = BountyChecker.hasItems(player, inv, bounty) ?: return false

        //val errMsg = "§4You haven't completed all of the requirements for this bounty."

        val entitiesFulfilled = BountyChecker.hasEntitiesFulfilled(bounty)
        if (!entitiesFulfilled) {
            //player.sendTranslation("bountiful.requirements.mobs.needed")
            return false
        }


        return if (!atBoard && BountifulMod.config.cashInAtBountyBoard) {
            //player.sendTranslation("bountiful.requirements.met")
            false
        } else {
            //player.sendTranslation("bountiful.bounty.fulfilled")
            // Reduce count of relevant prerequisite stacks
            BountyChecker.takeItems(player, inv, bounty, invItemsAffected)

            // Remove bounty note
            player.setHeldItem(hand, ItemStack.EMPTY)

            // Reward player with rewards
            BountyChecker.rewardItems(player, bounty, bountyItem)

            val bountyRarity = EnumBountyRarity.getRarityFromInt(bounty.rarity)

            // Increment stats

            // TODO Reimplement Scoreboard Stats
            //player.addStat(BountifulStats.bountiesCompleted)
            //player.addStat(bountyRarity.stat)

            // Give XP
            player.giveExperiencePoints(bountyRarity.xp)

            true
        }


    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {

        if (worldIn.isRemote) {
            return super.onItemRightClick(worldIn, playerIn, handIn)
        }

        cashIn(playerIn, handIn, atBoard = false)

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    // Don't flail arms randomly on NBT update
    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return slotChanged
    }

}