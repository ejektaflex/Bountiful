package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.util.readOnlyCopy
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class BoardBountySlot(val inv: BoardInventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?): Boolean {
        return false
    }

    override fun canTakeItems(player: PlayerEntity): Boolean {
        if (player is ServerPlayerEntity) {
            val board = player.world.getBlockEntity(inv.pos) as? BoardBlockEntity ?: return false
            // Mask all matching bounties
            val matchingMaskIndices = board.fullInventoryCopy().readOnlyCopy
                .mapIndexed { indexI, itemStack ->
                    if (ItemStack.canCombine(stack, itemStack)) {
                        indexI
                    } else {
                        null
                    }
                }.filterNotNull()
            for (newIndex in matchingMaskIndices) {
                board.maskFor(player).add(newIndex)
            }
        }
        super.onTakeItem(player, stack)
        return true
    }

}