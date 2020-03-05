package ejektaflex.bountiful.gui.slot

import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class DecreeSlot(val board: BoardTE, index: Int, x: Int, y: Int) : SlotItemHandler(board.handler, index, x, y) {
    override fun onSlotChanged() {
        board.markDirty()
    }

    override fun canTakeStack(playerIn: PlayerEntity): Boolean {
        return super.canTakeStack(playerIn) && board.numDecrees > 1
    }

    // The only valid items are ItemBounty with valid BountyData.
    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item is ItemDecree
    }

}