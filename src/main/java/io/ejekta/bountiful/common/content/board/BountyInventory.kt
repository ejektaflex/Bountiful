package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.content.BountyItem
import io.ejekta.bountiful.common.util.content
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

class BountyInventory : SimpleInventory(SIZE) {

    fun addBounty(slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        setStack(slot, BountyItem.create(data))
    }

    val numBounties: Int
        get() = content.count { it.item == BountifulContent.BOUNTY_ITEM }

    fun addRandomBounty(data: BountyData? = null): Int {
        val slot = bountySlots.random()
        setStack(slot, BountyItem.create(data))
        return slot
    }

    fun removeRandomBounty(except: Int) {
        setStack((bountySlots - except).random(), ItemStack.EMPTY)
    }

    fun cloned(blacklist: List<ItemStack>): BountyInventory {
        val newInv = BountyInventory()
        for (i in 0 until size()) {
            val stack = getStack(i)
            // do not sync over any stacks the player had in their inventory
            if (!blacklist.any { it.item is BountyItem && ItemStack.areTagsEqual(it, stack) }) {
                newInv.setStack(i, stack.copy())
            }
        }
        return newInv
    }

    companion object {
        const val SIZE = 21
        val bountySlots = 0 until 21
    }
}