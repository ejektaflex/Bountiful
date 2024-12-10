package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.util.readOnlyCopy
import net.minecraft.entity.player.Player
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayer

class BoardBountySlot(private val inv: BoardInventory, val usingPlayer: Player, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?): Boolean {
        return false
    }

    override fun canTakeItems(player: Player): Boolean {
        if (player is ServerPlayer) {
            val board = player.world.getBlockEntity(inv.pos) as? BoardBlockEntity ?: return false
            // Mask all matching bounties
            val matchingMaskIndices = board.fullInventoryCopy().readOnlyCopy
                .mapIndexed { indexI, itemStack ->
                    if (ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
                        indexI
                    } else {
                        null
                    }
                }.filterNotNull()
            // Add to mask
            for (newIndex in matchingMaskIndices) {
                board.maskFor(player).add(newIndex)
            }
        }
        super.onTakeItem(player, stack)
        return true
    }

    override fun onTakeItem(player: Player, stack: ItemStack) {
        if (stack.item is BountyItem) {
            BountyStack(stack).setPickedUp(player.world.time)
        }
        if (usingPlayer is ServerPlayer) {
            usingPlayer.incrementStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
        }
        super.onTakeItem(player, stack)
    }

}