package ejektaflex.bountiful.gui.slot

import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class BountySlot(val board: BoardTileEntity, index: Int, x: Int, y: Int) : SlotItemHandler(board.handler, index, x, y) {
    override fun onSlotChanged() {
        board.markDirty()
    }

    // The only valid items are ItemBounty with valid BountyData.
    override fun isItemValid(stack: ItemStack): Boolean {
        return if (stack.item is ItemBounty) {
            return BountyData.isValidBounty(stack)
        } else {
            false
        }
    }
}