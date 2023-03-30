package io.ejekta.bountiful.content.board

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos


class BoardInventory(
    val pos: BlockPos,
    bountySrc: Inventory = BountyInventory(),
    decreeSrc: Inventory = SimpleInventory(3)
) : DoubleInventory(
    bountySrc,
    decreeSrc
) {
    val selectedInv = SimpleInventory(1)

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

}