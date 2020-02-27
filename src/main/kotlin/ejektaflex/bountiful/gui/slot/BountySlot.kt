package ejektaflex.bountiful.gui.slot

import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class BountySlot(val board: BoardTE, index: Int, x: Int, y: Int) : SlotItemHandler(board.getTheHandler()!!, index, x, y) {
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