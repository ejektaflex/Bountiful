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

    fun cloned(): BountyInventory {
        val newInv = BountyInventory()
        for (i in 0 until size()) {
            newInv.setStack(i, getStack(i).copy())
        }
        return newInv
    }

    companion object {
        const val SIZE = 21
        val bountySlots = 0 until 21
    }
}