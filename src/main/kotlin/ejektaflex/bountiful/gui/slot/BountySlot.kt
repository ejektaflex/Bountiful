package ejektaflex.bountiful.gui.slot

//import ejektaflex.bountiful.advancement.BountifulTriggers
import ejektaflex.bountiful.BountifulStats
import ejektaflex.bountiful.block.BoardBlockEntity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class BountySlot(val board: BoardBlockEntity, index: Int, x: Int, y: Int) : SlotItemHandler(board.handler, index, x, y) {
    override fun onSlotChanged() {
        board.markDirty()
    }

    override fun onTake(thePlayer: PlayerEntity, stack: ItemStack): ItemStack {
        thePlayer.addStat(BountifulStats.BOUNTIES_TAKEN, 1)
        if (!thePlayer.world.isRemote) {
            //BountifulTriggers.BOUNTY_TAKEN.trigger((thePlayer as ServerPlayerEntity).advancements)
        }
        return super.onTake(thePlayer, stack)
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