package ejektaflex.bountiful.gui.slot

import ejektaflex.bountiful.block.BoardBlockEntity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class BountySlot(val board: BoardBlockEntity, index: Int, x: Int, y: Int) : SlotItemHandler(board.handler, index, x, y) {

    override fun onTake(thePlayer: Player, stack: ItemStack) {
        board.setChanged()
    }

    // The only valid items are ItemBounty with valid BountyData.

    override fun mayPlace(stack: ItemStack): Boolean {
        return if (stack.item is ItemBounty) {
            return BountyData.isValidBounty(stack)
        } else {
            false
        }
    }

}