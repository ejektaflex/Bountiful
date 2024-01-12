package io.ejekta.bountiful.content.board

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos


class BoardInventory(
    val pos: BlockPos,
    val bountySrc: Inventory = BountyInventory(),
    val decreeSrc: Inventory = SimpleInventory(3)
) : DoubleInventory(
    bountySrc,
    decreeSrc
) {
    private val selectedInv = SimpleInventory(1)

    var selectedIndex: Int? = null
        private set

    fun selected(): ItemStack = getStack(selectedIndex ?: -1)

    fun select(index: Int) {
        selectedIndex = index
        setStack(-1, getStack(index))
    }

    override fun isValid(slot: Int, stack: ItemStack?): Boolean {
        println("Validity check")
        return slot < 0 || super.isValid(slot, stack)
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return if (slot < 0) {
            selectedInv.removeStack(0)
        } else {
            super.removeStack(slot, amount)
        }
    }

    override fun canPlayerUse(player: PlayerEntity) = true

    // Get selected inv stack if slot index is -1
    override fun getStack(slot: Int): ItemStack {
        return if (slot < 0) {
            selectedInv.getStack(0)
        } else {
            super.getStack(slot)
        }
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        if (slot < 0) {
            selectedInv.setStack(0, stack)
        } else {
            super.setStack(slot, stack)
        }
    }

    override fun removeStack(slot: Int): ItemStack {
        if (slot == selectedIndex) {
            selectedInv.clear()
            selectedIndex = null
        }
        return super.removeStack(slot)
    }

    companion object {
        const val BOUNTY_SIZE = 21
        val BOUNTY_RANGE = 0 until BOUNTY_SIZE
        val DECREE_RANGE = BOUNTY_SIZE until BOUNTY_SIZE + 3
        val INVENTORY_RANGE = 24..50
        val HOTBAR_RANGE = 51..59
        val ENTIRE_PLAYER_INV = 24..59
    }

}