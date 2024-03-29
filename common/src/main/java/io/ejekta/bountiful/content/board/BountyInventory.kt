package io.ejekta.bountiful.content.board

import net.minecraft.inventory.SimpleInventory

class BountyInventory : SimpleInventory(BoardInventory.BOUNTY_SIZE) {

    fun cloned(mask: Set<Int>): BountyInventory {
        val newInv = BountyInventory()
        val valid = (0 until size()).filter { it !in mask }
        for (i in valid) {
            val stack = getStack(i)
            newInv.setStack(i, stack.copy())
        }
        return newInv
    }

    fun clone(): BountyInventory {
        return cloned(setOf())
    }
}