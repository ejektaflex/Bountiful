package ejektaflex.bountiful.logic

import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.data.BountyData
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

object BountyChecker {

    /**
     * Simply checks whether the two stacks are the same item and have the same NBT data.
     */
    private fun validStackCheck(stack: ItemStack, other: ItemStack): Boolean {
        return stack.isItemEqualIgnoreDurability(other) && ItemStack.areItemStackTagsEqual(stack, other)
    }

    fun hasItems(player: PlayerEntity, inv: NonNullList<ItemStack>, data: BountyData): List<ItemStack>? {
        /*
        val stackPicked = data.objectives.content.mapNotNull { it as? BountyEntryStack }

        println(stackPicked)

        val prereqItems = inv.filter { invItem ->
            stackPicked.any { picked ->
                picked.itemStack?.isItemEqualIgnoreDurability(invItem) == true
            }
        }

        println("Prereq items: $prereqItems")

        // Check to see if bounty meets all prerequisites
        val hasAllItems = stackPicked.all { picked ->
            val stacksMatching = prereqItems.filter { validStackCheck(it, picked.itemStack!!) }
            val hasEnough = stacksMatching.sumBy { it.count } >= picked.amount
            if (!hasEnough) {
                //player.sendTranslation("bountiful.cannot.fulfill")
            }
            hasEnough
        }

        return if (hasAllItems) {
            prereqItems
        } else {
            null
        }

         */
        return null
    }

    fun takeItems(player: PlayerEntity, inv: NonNullList<ItemStack>, data: BountyData, matched: List<ItemStack>) {
        /*
        // If it does, reduce count of all relevant stacks
        data.objectives.content.mapNotNull { it as? BountyEntryStack }.forEach { picked ->
            val stacksToChange = matched.filter { validStackCheck(it, picked.itemStack!!) }
            for (stack in stacksToChange) {
                if (picked.amount == 0) {
                    break
                }
                val amountToRemove = min(stack.count, picked.amount)
                stack.count -= amountToRemove
                picked.amount -= amountToRemove
            }
        }

         */
    }

    /**
     * Tries to tick all relevant entities on Bounty. Returns true if there are none left
     */
    fun tryTakeEntities(player: PlayerEntity, data: BountyData, bounty: ItemStack, entity: LivingEntity) {
        // Don't try take entities from an expired bounty.
        if (data.hasExpired(player.world)) {
            return
        }

        /*

        val bountyEntities = data.objectives.content.mapNotNull { it as? BountyEntryEntity }

        bountyEntities.forEach { picked ->
            //if (picked.entityEntry?.registryName?.toString() == entity.registryName?.toString()) {
                if (picked.killedAmount < picked.amount) {
                    picked.killedAmount++
                }
            //}
        }
        bounty.tag = data.serializeNBT()

         */
    }

    fun hasEntitiesFulfilled(data: BountyData): Boolean {
        /*
        val bountyEntities = data.objectives.content.mapNotNull { it as? BountyEntryEntity }
        return if (bountyEntities.isEmpty()) {
            true
        } else {
            bountyEntities.all { it.killedAmount == it.amount }
        }

         */
        return false
    }

    fun rewardItems(player: PlayerEntity, data: BountyData, bountyItem: ItemStack) {

        /*

        // Reward player with rewards
        data.rewards.content.forEach { reward ->
            var amountNeededToGive = reward.amount
            val stacksToGive = mutableListOf<ItemStack>()
            while (amountNeededToGive > 0) {
                val stackSize = min(amountNeededToGive, bountyItem.maxStackSize)
                //val newStack = reward.itemStack!!.copy().apply { count = stackSize }

                //stacksToGive.add(newStack)
                amountNeededToGive -= stackSize
            }
            stacksToGive.forEach { stack ->
                ItemHandlerHelper.giveItemToPlayer(player, stack)
            }
        }

         */
    }


}