package ejektaflex.bountiful.item

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.api.logic.BountyNBT
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.logic.BountyData
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.max
import kotlin.math.min


class ItemBounty : Item(), IItemBounty {

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        if (stack.hasTagCompound()) {
            val bounty = BountyData().apply { deserializeNBT(stack.tagCompound!!) }
            for (line in bounty.tooltipInfo(worldIn!!)) {
                tooltip.add(line)
            }
        }
    }

    override fun getBountyData(stack: ItemStack): BountyData {
        if (stack.hasTagCompound() && stack.item is ItemBounty) {
            return BountyData().apply { deserializeNBT(stack.tagCompound!!) }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty or has no NBT data!")
        }
    }

    override fun getRarity(stack: ItemStack): EnumRarity {
        return if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.Rarity.key)) {
            EnumBountyRarity.getRarityFromInt(stack.tagCompound!!.getInteger(BountyNBT.Rarity.key)).itemRarity
        } else {
            super.getRarity(stack)
        }
    }

    override fun removeTimestamp(stack: ItemStack) {
        if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.BountyStamp.key) && stack.tagCompound!!.hasKey(BountyNBT.BountyTime.key) ) {
            stack.tagCompound!!.apply {
                //setLong(BountyNBT.BountyTime.key, timeLeft(stack))
                //removeTag(BountyNBT.BountyStamp.key)
            }
        }
    }

    private fun tickNumber(stack: ItemStack, amount: Int, key: String): Boolean {
        if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(key)) {
            var time = stack.tagCompound!!.getInteger(key)
            if (time > 0) {
                time -= amount
            }
            if (time < 0) {
                time = 0
            }
            stack.tagCompound!!.setInteger(key, time)
            return (time <= 0)
        }
        return true
    }

    override fun tryExpireBountyTime(stack: ItemStack) {
        if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.BountyTime.key)) {
            stack.tagCompound!!.setInteger(BountyNBT.BountyTime.key, 0)
        }
    }

    override fun timeLeft(stack: ItemStack): Long {
        if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.BountyTime.key)) {
            if (stack.tagCompound!!.hasKey(BountyNBT.BountyStamp.key)) {
                val tag = stack.tagCompound
                return max(tag!!.getLong(BountyNBT.BountyStamp.key) - tag.getLong(BountyNBT.BountyTime.key), 0)
            }

        }
        return 0L
    }

    override fun tickBoardTime(stack: ItemStack): Boolean {
        return tickNumber(stack, BountyData.boardTickFreq.toInt(), BountyNBT.BoardStamp.key)
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return super.getItemStackDisplayName(stack) + if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BountyNBT.Rarity.key)) {
             " (${EnumBountyRarity.getRarityFromInt(stack.tagCompound!!.getInteger(BountyNBT.Rarity.key))})"
        } else {
            ""
        }
    }

    fun ensureTimerStarted(stack: ItemStack, worldIn: World) {
        if (stack.item is ItemBounty && stack.hasTagCompound() && !stack.tagCompound!!.hasKey(BountyNBT.BountyStamp.key)) {
            stack.tagCompound!!.setLong(BountyNBT.BountyStamp.key, worldIn.totalWorldTime)
        }
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity?, itemSlot: Int, isSelected: Boolean) {
        if (worldIn.totalWorldTime % BountyData.bountyTickFreq == 1L) {
            ensureTimerStarted(stack, worldIn)
        }
    }

    override fun ensureBounty(stack: ItemStack, worldIn: World) {
        if (stack.item is ItemBounty) {
            if (!stack.hasTagCompound()) {
                stack.tagCompound = BountyCreator.create().serializeNBT().apply {
                    this.removeTag(BountyNBT.BountyStamp.key)
                    this.setLong(BountyNBT.BoardStamp.key, worldIn.totalWorldTime)
                }
            }
        } else {
            throw Exception("${stack.displayName} is not an ItemBounty, so you cannot generate bounty data for it!")
        }
    }

    // Used to cash in the bounty for a reward
    fun cashIn(player: EntityPlayer, hand: EnumHand, atBoard: Boolean = false): Boolean {
        val bountyItem = player.getHeldItem(hand)
        if (!bountyItem.hasTagCompound()) {
            ensureBounty(player.getHeldItem(hand), player.world)
            return false
        }

        val inv = player.inventory.mainInventory
        val bounty = BountyData().apply { deserializeNBT(bountyItem.tagCompound!!) }

        val prereqItems = inv.filter { invItem ->
            bounty.toGet.any { gettable ->
                gettable.first.isItemEqualIgnoreDurability(invItem)
            }
        }

        if (bounty.timeLeft(player.world) <= 0) {
            player.sendMessage("§4This bounty is expired.")
            return false
        }

        println("Prereq items: $prereqItems")

        // Check to see if bounty meets all prerequisites
        val hasAllGets = bounty.toGet.all { gettable ->
            val stacksToChange = prereqItems.filter { it.isItemEqualIgnoreDurability(gettable.first) }
            val stackSum = stacksToChange.sumBy { it.count }
            val amountNeeded = gettable.second
            val hasEnough = stackSum >= amountNeeded
            if (!hasEnough) {
                player.sendMessage(TextComponentString("§cCannot fulfill bounty, you don't have everything needed!"))
            }

            hasEnough
        }


        if (hasAllGets) {
            if (!atBoard && Bountiful.config.cashInAtBountyBoard) {
                player.sendMessage(TextComponentString("§aBounty requirements met. Fullfill your bounty by right clicking on a bounty board."))
                return false
            } else {
                player.sendMessage(TextComponentString("§aBounty Fulfilled!"))
            }

            // If it does, reduce count of all relevant stacks
            bounty.toGet.forEach { gettable ->
                var amountNeeded = gettable.second
                val stacksToChange = prereqItems.filter { it.isItemEqualIgnoreDurability(gettable.first) }
                for (stack in stacksToChange) {
                    val amountToRemove = min(stack.count, amountNeeded)
                    stack.count -= amountToRemove
                    amountNeeded -= amountToRemove
                }
            }

            // Remove bounty note
            player.setHeldItem(hand, ItemStack.EMPTY)

            // Reward player with rewards
            bounty.rewards.forEach { reward ->
                var amountNeededToGive = reward.second
                val stacksToGive = mutableListOf<ItemStack>()
                while (amountNeededToGive > 0) {
                    val stackSize = min(amountNeededToGive, maxStackSize)
                    val newStack = reward.first.copy().apply { count = stackSize }
                    stacksToGive.add(newStack)
                    amountNeededToGive -= stackSize
                }
                stacksToGive.forEach { stack ->
                    ItemHandlerHelper.giveItemToPlayer(player, stack)
                }
            }
            return true
        } else {
            return false
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {

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