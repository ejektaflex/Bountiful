package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.messages.ServerPlayerStatus
import io.ejekta.bountiful.util.readOnlyCopy
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class BoardBountySlot(private val inv: BoardInventory, val usingPlayer: PlayerEntity, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
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
            // Add to mask
            for (newIndex in matchingMaskIndices) {
                board.maskFor(player).add(newIndex)
            }
        }
        super.onTakeItem(player, stack)
        return true
    }

    override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
        if (stack.item is BountyItem) {
            BountyInfo.setPickedUp(stack, player.world.time)
        }
        if (usingPlayer is ServerPlayerEntity) {
            usingPlayer.incrementStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
        }
        super.onTakeItem(player, stack)
    }

}