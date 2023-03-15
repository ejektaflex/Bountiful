package ejektaflex.bountiful.gui

import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.block.BoardBlockEntity
import ejektaflex.bountiful.gui.slot.BountySlot
import ejektaflex.bountiful.gui.slot.DecreeSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import net.minecraftforge.items.wrapper.InvWrapper


class BoardMenu(val windowId: Int, val inv: Inventory, val boardTE: BoardBlockEntity) : AbstractContainerMenu(BountifulContent.BOARDCONTAINER, windowId) {

    constructor(inWindow: Int, inInv: Inventory, data: PacketBuffer) : this(inWindow, inInv, getTileEntity(inInv, data))

    companion object {
        fun getTileEntity(inv: Inventory, data: PacketBuffer): BoardBlockEntity {
            val tileAtPos = inv.player.level.getBlockEntity(data.readBlockPos())
            return tileAtPos as? BoardBlockEntity ?: throw IllegalStateException("Tile entity is not correct! $tileAtPos")
        }
    }

    private val playerInvHandler: IItemHandler by lazy {
        InvWrapper(inv)
    }

    init {

        val bRows = 3
        val bCols = 7

        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                addSlot(BountySlot(boardTE, k + j * bCols, 8 + k * 18, 18 + j * 18))
            }

            addSlot(DecreeSlot(boardTE, BoardBlockEntity.SIZE - 3 + j, 19 + 7 * 18, 18 + j * 18))
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
    override fun stillValid(playerIn: Player): Boolean {
        return true
    }

    // abomination
    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            val containerSlots = inventorySlots.size - player.inventory.mainInventory.size

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