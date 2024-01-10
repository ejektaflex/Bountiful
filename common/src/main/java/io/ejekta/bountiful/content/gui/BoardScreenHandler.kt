package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.board.BountyInventory
import io.ejekta.bountiful.messages.ServerPlayerStatus
import io.ejekta.kambrik.gui.screen.KambrikScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos


class BoardScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: BoardInventory,
    doneProp: PropertyDelegate
) : KambrikScreenHandler<BoardScreenHandler, BoardInventory>(BountifulContent.BOARD_SCREEN_HANDLER, syncId) {

    private val doneProperty = doneProp

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory,
        BoardInventory(BlockPos.ORIGIN), ArrayPropertyDelegate(1)
    )

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun onClosed(player: PlayerEntity?) {
        inventory.onClose(player)
    }

    fun getTotalNumComplete(): Int {
        return doneProperty.get(0)
    }

    override fun quickMove(player: PlayerEntity, invSlot: Int): ItemStack {
        if (player.server == null) {
            if (invSlot in BoardInventory.BOUNTY_RANGE) {
                ServerPlayerStatus.Type.BOUNTY_TAKEN.sendToServer()
            } else {
                if (invSlot > BoardInventory.DECREE_RANGE.last + 1) {
                    if (getSlot(invSlot).stack.item == BountifulContent.DECREE_ITEM) {
                        ServerPlayerStatus.Type.DECREE_PLACED.sendToServer()
                    }
                } else {
                    //println("Quick moved from decree area")
                }
            }
        }
        return super.quickMove(player, invSlot)
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
        addProperties(doneProperty)

        val boardInv = inventory

        val bRows = 3
        val bCols = 7

        val bountySlotSize = 18
        val adjustX = 173
        val adjustY = 0

        // Bounties
        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                // Welcome to jank! TODO do this in a better way for client<->server sync
                addSlot(BoardBountySlot(inventory, playerInventory.player, k + j * bCols, 8 + k * bountySlotSize + adjustX, 18 + j * bountySlotSize + adjustY))
            }
        }

        // Active Slot
        addSlot(BoardBountySlot(inventory, playerInventory.player, -1, 216 + 500000, 31))


        // Decrees
        for (j in 0 until 3) {
            addSlot(BoardDecreeSlot(boardInv, playerInventory.player, inventory.size() - 3 + j, 317, 18 + (j * 18)))
        }

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 181, 84)

//        if (playerInventory.player !is ServerPlayerEntity) {
//            println("Opened screen as client player, sending data")
//            BoardDoneRequest(playerInventory.player.world.dimensionKey.value, boardInv.pos)
//        }

        //val pd = PropertyDelegate

    }
}

