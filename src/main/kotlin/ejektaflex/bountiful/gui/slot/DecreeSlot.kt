package ejektaflex.bountiful.gui.slot

import ejektaflex.bountiful.block.BoardBlockEntity
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class DecreeSlot(val board: BoardBlockEntity, index: Int, x: Int, y: Int) : SlotItemHandler(board.handler, index, x, y) {

    init {
        //setBackgroundName(BountifulMod.MODID + ":bg")
    }

    override fun onSlotChanged() {
        board.markDirty()
    }

    override fun canTakeStack(playerIn: PlayerEntity): Boolean {
        return super.canTakeStack(playerIn) && board.numDecrees > 1
    }

    override fun isItemValid(stack: ItemStack): Boolean {
        return super.isItemValid(stack) && stack.item is ItemDecree
    }

}