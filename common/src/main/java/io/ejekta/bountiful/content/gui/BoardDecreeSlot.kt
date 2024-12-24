package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.util.currentBoardInteracting
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class BoardDecreeSlot(inv: BoardInventory, val usingPlayer: Player, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun mayPlace(stack: ItemStack) = stack.item == BountifulContent.DECREE_ITEM

    override fun mayPickup(playerEntity: Player): Boolean {
        return container.countItem(BountifulContent.DECREE_ITEM) > 1
    }

    override fun safeInsert(stack: ItemStack, count: Int): ItemStack {
        if (usingPlayer is ServerPlayer) {
            usingPlayer.currentBoardInteracting?.onUserPlacedDecree(usingPlayer, stack)
        }
        return super.safeInsert(stack, count)
    }
}