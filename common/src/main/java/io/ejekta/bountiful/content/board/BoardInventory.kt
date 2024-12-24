package io.ejekta.bountiful.content.board

import net.minecraft.core.BlockPos
import net.minecraft.world.CompoundContainer
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack


class BoardInventory(
    val pos: BlockPos,
    val bountySrc: Container = BountyInventory(),
    val decreeSrc: Container = SimpleContainer(3)
) : CompoundContainer(
    bountySrc,
    decreeSrc
) {
    private val selectedInv = SimpleContainer(1)

    var selectedIndex: Int? = null
        private set

    fun selected(): ItemStack = getItem(selectedIndex ?: -1)

    fun select(index: Int) {
        selectedIndex = index
        setItem(-1, getItem(index))
    }

    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
        println("Validity check")
        return slot < 0 || super.canPlaceItem(slot, stack)
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        return if (slot < 0) {
            selectedInv.removeItemNoUpdate(0)
        } else {
            super.removeItem(slot, amount)
        }
    }

    // "can player use"
    override fun stillValid(player: Player) = true

    // Get selected inv stack if slot index is -1
    override fun getItem(slot: Int): ItemStack {
        return if (slot < 0) {
            selectedInv.getItem(0)
        } else {
            super.getItem(slot)
        }
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot < 0) {
            selectedInv.setItem(0, stack)
        } else {
            super.setItem(slot, stack)
        }
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == selectedIndex) {
            selectedInv.clearContent()
            selectedIndex = null
        }
        return super.removeItemNoUpdate(slot)
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