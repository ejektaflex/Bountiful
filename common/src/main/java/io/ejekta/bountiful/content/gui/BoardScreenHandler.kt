@file:Suppress("OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS")

package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.board.BountyInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.messages.ServerPlayerStatus
import io.ejekta.bountiful.util.currentBoardInteracting
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.gui.screen.KambrikScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
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

    fun attemptInsert(stack: ItemStack, slotRange: IntRange, backwards: Boolean = false): ItemStack? {
        return when (insertItem(stack, slotRange.first, slotRange.last + 1, backwards)) {
            false -> null
            true -> stack
        }
    }

    override fun quickMove(player: PlayerEntity, invSlot: Int): ItemStack {
        if (player is ServerPlayerEntity) {
            val stack = getSlot(invSlot).stack

            if (stack.item is BountyItem) {
                BountyInfo.setPickedUp(stack, player.world.time)
            }

            when (invSlot) {
                in BoardInventory.BOUNTY_RANGE -> {
                    val result = attemptInsert(stack, BoardInventory.HOTBAR_RANGE) ?: attemptInsert(stack, BoardInventory.INVENTORY_RANGE) ?: ItemStack.EMPTY
                    if (result != ItemStack.EMPTY) {
                        player.incrementStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
                    }
                    return result
                }
                in BoardInventory.DECREE_RANGE -> {
                    return attemptInsert(stack, BoardInventory.HOTBAR_RANGE) ?: attemptInsert(stack, BoardInventory.INVENTORY_RANGE) ?: ItemStack.EMPTY
                }
                else -> {
                    when (stack.item) {
                        // If it's a decree in the inventory, try put in the decrees spot
                        is DecreeItem -> {
                            return attemptInsert(stack, BoardInventory.DECREE_RANGE, backwards = false).also {
                                if (it != null) {
                                    player.currentBoardInteracting?.onUserPlacedDecree(player, stack)
                                }
                            } ?: ItemStack.EMPTY
                        }
                        // If it's a bounty already in the inventory, swap main and hotbar
                        else -> {
                            when (invSlot) {
                                in BoardInventory.HOTBAR_RANGE -> {
                                    return attemptInsert(stack, BoardInventory.INVENTORY_RANGE) ?: ItemStack.EMPTY
                                }
                                in BoardInventory.INVENTORY_RANGE -> {
                                    return attemptInsert(stack, BoardInventory.HOTBAR_RANGE) ?: ItemStack.EMPTY
                                }
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY
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
                addSlot(BoardBountySlot(inventory, playerInventory.player, k + j * bCols, 8 + k * bountySlotSize + adjustX, 18 + j * bountySlotSize + adjustY))
            }
        }


        // Decrees
        for (j in 0 until 3) {
            addSlot(BoardDecreeSlot(boardInv, playerInventory.player, inventory.size() - 3 + j, 317, 18 + (j * 18)))
        }

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 181, 84)


        // Active Slot
        addSlot(BoardBountySlot(inventory, playerInventory.player, -1, 216 + 500000, 31))
    }
}

