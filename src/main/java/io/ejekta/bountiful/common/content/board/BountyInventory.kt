package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountyItem
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

class BountyInventory : SimpleInventory(SIZE) {

    fun addBounty(slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        setStack(slot, BountyItem.create(data))
    }

    fun addRandomBounty(data: BountyData? = null): Int {
        val slot = bountySlots.random()
        setStack(slot, BountyItem.create(data))
        return slot
    }

    fun removeRandomBounty(except: Int) {
        setStack((bountySlots - except).random(), ItemStack.EMPTY)
    }

    companion object {
        const val SIZE = 21
        val bountySlots = 0 until 21
    }
}