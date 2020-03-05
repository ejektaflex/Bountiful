package ejektaflex.bountiful.gui

import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.ModContent
import ejektaflex.bountiful.gui.slot.BountySlot
import ejektaflex.bountiful.gui.slot.DecreeSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import net.minecraftforge.items.wrapper.InvWrapper


class BoardContainer(id: Int, val world: World, val pos: BlockPos, val inv: PlayerInventory) : Container(ModContent.Guis.BOARDCONTAINER, id) {

    private val boardTE: BoardTileEntity by lazy {
        world.getTileEntity(pos) as BoardTileEntity
    }

    private val playerInvHandler: IItemHandler by lazy {
        InvWrapper(inv)
    }

    init {

        val bRows = 3
        val bCols = 7

        for (j in 0 until bRows) {
            // EIGHT! 7! NOT NINE! We need to make the DecreeSlots on the right side.
            for (k in 0 until bCols) {
                println("Made slot with index $j $k: ${k + j * bCols}")
                addSlot(BountySlot(boardTE, k + j * bCols, 8 + k * 18, 18 + j * 18))
            }

            println("Making new slot with index ${BoardTileEntity.SIZE - 2 + j}")
            addSlot(DecreeSlot(boardTE, BoardTileEntity.SIZE - 3 + j, 19 + 7 * 18, 18 + j * 18))
        }

        for (j in 0..2) {
            for (k in 0..8) {
                this.addSlot(SlotItemHandler(playerInvHandler, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 - 19))
            }
        }

        for (i1 in 0..8) {
            this.addSlot(SlotItemHandler(playerInvHandler, i1, 8 + i1 * 18, 142))
        }
    }

    // TODO Maybe use isWithinUsableDistance later on
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    // abomination
    override fun transferStackInSlot(player: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            val containerSlots = inventorySlots.size - player!!.inventory.mainInventory.size

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, itemstack1)
        }

        return itemstack
    }


}