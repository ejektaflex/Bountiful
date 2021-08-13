package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class BoardBountySlot(val inv: BoardInventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?) = false
    override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
        if (player is ServerPlayerEntity) {
            val board = player.world.getBlockEntity(inv.pos) as? BoardBlockEntity
            board?.addToMask(player, index)
        }
        super.onTakeItem(player, stack)
    }
}