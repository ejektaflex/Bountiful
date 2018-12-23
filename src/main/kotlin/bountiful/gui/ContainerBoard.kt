package bountiful.gui

import bountiful.block.TileEntityBountyBoard
import bountiful.item.ItemBounty
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerBoard(playerInv: InventoryPlayer, boardTE: TileEntityBountyBoard) : Container() {

    init {
        val inventory = boardTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)

        for (j in 0 until 3) {
            for (k in 0 until 9) {
                addSlotToContainer(object : SlotItemHandler(inventory, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    override fun onSlotChanged() {
                        boardTE.markDirty()
                    }

                    override fun isItemValid(stack: ItemStack): Boolean {
                        return stack.item is ItemBounty
                    }
                })
            }
        }

        for (j in 0..2) {
            for (k in 0..8) {
                this.addSlotToContainer(Slot(playerInv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 - 19))
            }
        }

        for (i1 in 0..8) {
            this.addSlotToContainer(Slot(playerInv, i1, 8 + i1 * 18, 142))
        }

    }

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return true
    }


    override fun transferStackInSlot(player: EntityPlayer?, index: Int): ItemStack {
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
