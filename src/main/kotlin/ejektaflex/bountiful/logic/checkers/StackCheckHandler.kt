package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.StackPartition
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

class StackCheckHandler() : CheckHandler<BountyEntryStack>() {

    val partMap = mutableMapOf<ItemStack, StackPartition>()

    override fun fulfill() {
        for (part in partMap) {
            part.value.shrink()
        }
    }

    override fun objectiveStatus(): Map<BountyEntry, BountyProgress> {
        partMap.clear()

        val stackObjs = data.objectives.content.filterIsInstance<BountyEntryStack>()

        val stackTypeObj = stackObjs.filter { it.type == "stack" }
        val tagTypeObj = stackObjs.filter { it.type == "tag" }

        //println("Objectives: $stackObjs")

        // For each stack objective
        //println("Checking stacks")
        val a = checkObjs(stackTypeObj)
        //println("Checking tags")
        val b = checkObjs(tagTypeObj).toMutableMap()

        for (key in a.keys) {
            b[key] = a.getValue(key)
        }

        //println("Fully reserved partitions: ${partMap.count { it.value.free == 0 }}")

        //println("B: $b")

        return b

    }

    private fun checkObjs(list: List<BountyEntryStack>): Map<BountyEntry, BountyProgress> {

        var succ = mutableMapOf<BountyEntry, BountyProgress>()

        // For each stack objective
        loop@ for (obj in list) {

            var neededForObj = obj.amount

            // Get all matching inventory stacks
            val invStacks = inv.filter { validStackCheck(obj.validStacks, it) }
            for (iStack in invStacks) {
                //println("Analyzing stack: $iStack")
                // Initialize the stack in the partmap
                if (iStack !in partMap) {
                    partMap[iStack] = StackPartition(iStack)
                }
                // Grab it
                val part = partMap[iStack]!!

                //println("PartMapSize: ${partMap.keys}")
                //println("Partition reserving in: $part")

                //println("Trying to reserve: $neededForObj")

                val leftOver = part.reserve(neededForObj)

                //println("Leftover after reserving: $leftOver")

                // If we have nothing leftover (AKA allocated it all), we are done with this item stack
                if (leftOver == 0) {
                    neededForObj = 0
                    break
                } else {
                    neededForObj = leftOver
                }
            }

            val amountGotten = obj.amount - neededForObj


            // TODO: Remove this
            when (neededForObj) {
                0 -> {
                    println("Got it all! / AKA SUCC 1 OBJ")
                    //succ[obj] = BountyProgress(obj.amount to obj.amount)
                }
                else -> {
                    println("Needed this many more: $neededForObj / AKA FAILED 1 OBJ")
                    //succ[obj] = BountyProgress.UNFINISHED
                    //break@loop
                }
            }

            succ[obj] = BountyProgress(amountGotten to obj.amount)


        }

        return succ

    }


    private fun validStackCheck(stacks: List<ItemStack>, other: ItemStack): Boolean {
        return stacks.any { stack -> stack.isItemEqualIgnoreDurability(other) && ItemStack.areItemStackTagsEqual(stack, other) }
    }


}