package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.kambrik.KambrikScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
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
        super.onClosed(player) // handle cursor stack
    }

    fun getTotalNumComplete(): Int {
        return doneProperty.get(0)
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

        val boardInv = inventory as BoardInventory

        val bRows = 3
        val bCols = 7

        val bountySlotSize = 18
        val adjustX = 173
        val adjustY = 0

        // Bounties
        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                // Welcome to jank! TODO do this in a better way for client<->server sync
                addSlot(BoardBountySlot(inventory, k + j * bCols, 8 + k * bountySlotSize + adjustX, 18 + j * bountySlotSize + adjustY))
            }
        }

        // Active Slot
        addSlot(BoardBountySlot(inventory, -1, 216 + 500000, 31))


        // Decrees
        for (j in 0 until 3) {
            addSlot(BoardDecreeSlot(boardInv, inventory.size() - 3 + j, 317, 18 + (j * 18)))
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

