package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.util.readOnlyCopy
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class BoardBountySlot(private val inv: BoardInventory, val usingPlayer: Player, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean {
        return false
    }

    override fun mayPickup(player: Player): Boolean {
        if (player is ServerPlayer) {
            val board = player.level().getBlockEntity(inv.pos) as? BoardBlockEntity ?: return false
            // Mask all matching bounties
            val matchingMaskIndices = board.fullInventoryCopy().readOnlyCopy
                .mapIndexed { indexI, itemStack ->
                    if (ItemStack.isSameItemSameComponents(item, itemStack)) {
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
        super.onTake(player, item)
        return true
    }

    override fun onTake(player: Player, stack: ItemStack) {
        if (stack.item is BountyItem) {
            BountyStack(stack).setPickedUp(player.level().gameTime)
        }
        if (usingPlayer is ServerPlayer) {
            usingPlayer.awardStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
        }
        super.onTake(player, stack)
    }

}