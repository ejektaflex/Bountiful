package io.ejekta.bountiful.content.board

import net.minecraft.world.SimpleContainer

class BountyInventory : SimpleContainer(BoardInventory.BOUNTY_SIZE) {

    fun cloned(mask: Set<Int>): BountyInventory {
        val newInv = BountyInventory()
        val valid = (0 until containerSize).filter { it !in mask }
        for (i in valid) {
            val stack = getItem(i)
            newInv.setItem(i, stack.copy())
        }
        return newInv
    }

    fun clone(): BountyInventory {
        return cloned(setOf())
    }
}