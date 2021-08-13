package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.data.messages.ClientUpdateBountySlot
import io.ejekta.bountiful.util.readOnlyCopy
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

class BountyInventory : SimpleInventory(SIZE) {

    fun addBounty(entity: BoardBlockEntity, slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        val item = BountyItem.create(data)

        // Update the GUI board inventories of all tracking players as well
        PlayerLookup.tracking(entity).forEach { player ->
            val handler = player.currentScreenHandler as? BoardScreenHandler
            handler?.let {
                println("Got curr handler of player")
                val boardInv = it.inventory
                boardInv.setStack(slot, item)
            }
        }

        setStack(slot, item)
    }

    fun removeBounty(entity: BoardBlockEntity, slot: Int) {

        PlayerLookup.tracking(entity).forEach { player ->
            val handler = player.currentScreenHandler as? BoardScreenHandler
            handler?.let {
                println("Got curr handler of player")
                val boardInv = it.inventory
                boardInv.removeStack(slot)
            }
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