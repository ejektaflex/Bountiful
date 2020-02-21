package ejektaflex.bountiful.logic

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.api.ext.modOriginName
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

class BountyCheck(val player: PlayerEntity, val data: IBountyData, val inv: NonNullList<ItemStack>) {

    val partMap = mutableMapOf<ItemStack, StackPartition>()

    fun checkStacks() {
        partMap.clear()

        val stackObjs = data.objectives.content.filterIsInstance<BountyEntryStack>()

        println("Objectives: $stackObjs")

        // For each stack objective
        for (obj in stackObjs) {

            var neededForObj = obj.amount

            // Get all matching inventory stacks
            val invStacks = inv.filter { validStackCheck(obj.itemStack ?: ItemStack.EMPTY, it) }
            for (iStack in invStacks) {
                println("Analyzing stack: $iStack")
                // Initialize the stack in the partmap
                if (iStack !in partMap) {
                    partMap[iStack] = StackPartition(iStack)
                }
                // Grab it
                val part = partMap[iStack]!!

                println("Trying to reserve: $neededForObj")

                val leftOver = part.reserve(neededForObj)

                println("Leftover after reserving: $leftOver")

                // If we have nothing leftover (AKA allocated it all), we are done with this item stack
                if (leftOver == 0) {
                    neededForObj = 0
                    break
                } else {
                    neededForObj = leftOver
                }
            }

            if (neededForObj == 0) {
                println("Got it all!")
            } else {
                println("Needed this many more: $neededForObj")
            }

        }


    }


    private fun validStackCheck(stack: ItemStack, other: ItemStack): Boolean {
        return stack.isItemEqualIgnoreDurability(other) && ItemStack.areItemStackTagsEqual(stack, other)
    }


}