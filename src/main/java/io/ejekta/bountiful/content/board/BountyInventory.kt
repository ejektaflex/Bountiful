package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.inventory.SimpleInventory

class BountyInventory : SimpleInventory(SIZE) {

    private fun modifyTrackedGuiInvs(entity: BoardBlockEntity, func: (inv: BoardInventory) -> Unit) {
        PlayerLookup.tracking(entity).forEach { player ->
            val handler = player.currentScreenHandler as? BoardScreenHandler
            handler?.let {
                val boardInv = it.inventory
                func(boardInv)
            }
        }
    }

    fun addBounty(entity: BoardBlockEntity, slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        val item = BountyItem.create(data)

        modifyTrackedGuiInvs(entity) {
            it.setStack(slot, item.copy())
        }

        setStack(slot, item)
    }

    fun removeBounty(entity: BoardBlockEntity, slot: Int) {
        modifyTrackedGuiInvs(entity) {
            it.removeStack(slot)
        }

        removeStack(slot)
    }

    fun cloned(mask: Set<Int>): BountyInventory {
        val newInv = BountyInventory()
        val valid = (0 until size()).filter { it !in mask }
        for (i in valid) {
            val stack = getStack(i)
            newInv.setStack(i, stack.copy())
        }
        return newInv
    }

    companion object {
        const val SIZE = 21
        val bountySlots = 0 until SIZE
    }
}