package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.board.BountyInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class BoardBountySlot(private val inv: Inventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?) = false
}