package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.data.bounty.AbstractBountyEntryStackLike
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryItem
import ejektaflex.bountiful.data.bounty.BountyEntryItemTag
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.StackPartition
import net.minecraft.item.ItemStack

class StackLikeCheckHandler : CheckHandler<BountyEntryItem>() {

    val partMap = mutableMapOf<ItemStack, StackPartition>()

    override fun fulfill() {
        for (part in partMap) {
            part.value.shrink()
        }
    }

    override fun objectiveStatus(): Map<BountyEntry, BountyProgress> {
        partMap.clear()

        val stackTypeObj = data.objectives.content.filterIsInstance<BountyEntryItem>()
        val tagTypeObj = data.objectives.content.filterIsInstance<BountyEntryItemTag>()

        // For each stack objective
        val a = checkObjs(stackTypeObj)
        val b = checkObjs(tagTypeObj).toMutableMap()

        for (key in a.keys) {
            b[key] = a.getValue(key)
        }

        return b

    }

    private fun checkObjs(list: List<AbstractBountyEntryStackLike>): Map<BountyEntry, BountyProgress> {

        val succ = mutableMapOf<BountyEntry, BountyProgress>()

        // For each stack objective
        for (obj in list) {

            var neededForObj = obj.amount

            // Get all matching inventory stacks
            val invStacks = inv.filter { validStackCheck(obj.validStacks, it) }
            for (iStack in invStacks) {

                // Initialize the stack in the partmap
                if (iStack !in partMap) {
                    partMap[iStack] = StackPartition(iStack)
                }
                // Grab it
                val part = partMap[iStack]!!

                val leftOver = part.reserve(neededForObj)

                // If we have nothing leftover (AKA allocated it all), we are done with this item stack
                if (leftOver == 0) {
                    neededForObj = 0
                    break
                } else {
                    neededForObj = leftOver
                }
            }

            succ[obj] = BountyProgress((obj.amount - neededForObj) to obj.amount)
        }

        return succ
    }

    private fun validStackCheck(stacks: List<ItemStack>, other: ItemStack): Boolean {
        return stacks.any { stack -> stack.isItemEqualIgnoreDurability(other) && ItemStack.areItemStackTagsEqual(stack, other) }
    }


}