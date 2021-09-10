package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.KambrikScreenHandler
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos


class BoardScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: BoardInventory
) : KambrikScreenHandler<BoardScreenHandler, BoardInventory>(BountifulContent.BOARD_SCREEN_HANDLER, syncId) {

    var totalDone = 0

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(syncId, playerInventory,
        BoardInventory(BlockPos.ORIGIN)
    ) {
        totalDone = buf.readInt()
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun close(player: PlayerEntity?) {
        inventory.onClose(player)
    }

    // Shift + Player Inv Slot
    override fun transferSlot(player: PlayerEntity, invSlot: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot: Slot? = slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val originalStack: ItemStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return newStack
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    init {
        checkSize(inventory, BoardBlock.BOUNTY_SIZE)
        //this.inventory = inventory
        //some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player)

        val boardInv = inventory as BoardInventory

        val bRows = 3
        val bCols = 7

        val bountySlotSize = 18
        val adjustX = 0
        val adjustY = 0

        // Bounties
        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                addSlot(BoardBountySlot(inventory as BoardInventory, k + j * bCols, 8 + k * bountySlotSize + adjustX + 1000, 18 + j * bountySlotSize + adjustY))
            }
        }

        // Decrees
        for (j in 0 until bRows) {
            addSlot(BoardDecreeSlot(boardInv, inventory.size() - 3 + j, 19 + 7 * 18 + 1000, 18 + j * 18))
        }

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 180, 64)

    }
}

