package io.ejekta.bountiful.kambrik

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

abstract class KambrikScreenHandler<S : ScreenHandler, INV : Inventory>(type: ScreenHandlerType<S>?, syncId: Int) : ScreenHandler(type, syncId) {

    abstract var inventory: INV

    protected fun <I : Inventory, S : Slot> makeSlotGrid(
        inventory: I,
        cols: Int,
        rows: Int,
        offX: Int = 0,
        offY: Int = 0,
        padding: Int = 0,
        startIndex: Int = 0,
        slotMaker: ( (inv: I, index: Int, x: Int, y: Int) -> S )? = null
    ) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val calcIndex = col + row * cols + startIndex
                val calcX = col * (18 + padding) + offX
                val calcY = row * (18 + padding) + offY
                if (slotMaker != null) {
                    addSlot(slotMaker(inventory, calcIndex, calcX, calcY))
                } else {
                    addSlot(Slot(inventory, calcIndex, calcX, calcY))
                }
            }
        }
    }

    override fun quickMove(player: PlayerEntity, invSlot: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot: Slot? = slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val originalStack: ItemStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return newStack
    }

    protected fun makePlayerInventoryGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makeSlotGrid<PlayerInventory, Slot>(playerInventory, 9, 3, offX, offY, startIndex = 9)
    }

    protected fun makePlayerHotbarGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makeSlotGrid<PlayerInventory, Slot>(playerInventory, 9, 1, offX, offY)
    }

    protected fun makePlayerDefaultGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makePlayerInventoryGrid(playerInventory, offX, offY)
        makePlayerHotbarGrid(playerInventory, offX, offY + 58)
    }

}