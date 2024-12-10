package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.util.currentBoardInteracting
import net.minecraft.entity.player.Player
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayer

class BoardDecreeSlot(inv: BoardInventory, val usingPlayer: Player, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?) = stack?.item == BountifulContent.DECREE_ITEM

    override fun canTakeItems(playerEntity: Player): Boolean {
        return inventory.count(BountifulContent.DECREE_ITEM) > 1
    }

    override fun insertStack(stack: ItemStack, count: Int): ItemStack {
        if (usingPlayer is ServerPlayer) {
            usingPlayer.currentBoardInteracting?.onUserPlacedDecree(usingPlayer, stack)
        }
        return super.insertStack(stack, count)
    }
}