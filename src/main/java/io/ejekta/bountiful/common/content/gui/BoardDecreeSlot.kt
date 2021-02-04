package io.ejekta.bountiful.common.content.gui

import io.ejekta.bountiful.common.bounty.logic.DecreeList
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.content.board.BoardInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class BoardDecreeSlot(inv: BoardInventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {

    override fun canInsert(stack: ItemStack?) = stack?.item == BountifulContent.DECREE_ITEM

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        return inventory.count(BountifulContent.DECREE_ITEM) > 1
    }

    override fun setStack(stack: ItemStack?) {
        stack?.let {
            DecreeList.edit(it) {
                if (ids.isEmpty() && BountifulContent.Decrees.isNotEmpty()) {
                    ids.add(BountifulContent.Decrees.random().id)
                }
            }
        }
        super.setStack(stack)
    }

    override fun onStackChanged(originalItem: ItemStack?, itemStack: ItemStack?) {

        super.onStackChanged(originalItem, itemStack)
    }

}