@file:Suppress("OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS")

package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.util.currentBoardInteracting
import io.ejekta.kambrik.gui.screen.KambrikContainerMenu
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack


class BoardScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: Inventory,
    override var container: BoardInventory,
    doneProp: ContainerData
) : KambrikContainerMenu<BoardScreenHandler, BoardInventory>(BountifulContent.BOARD_SCREEN_HANDLER, syncId) {
    private val doneProperty = doneProp

    constructor(syncId: Int, playerInventory: Inventory) : this(syncId, playerInventory,
        BoardInventory(BlockPos.ZERO), SimpleContainerData(1)
    )

    // 'Can use'
    override fun stillValid(player: Player): Boolean {
        return container.stillValid(player)
    }


    override fun removed(player: Player) {
        container.stopOpen(player)
        super.removed(player) // handle cursor stack
    }

    fun getTotalNumComplete(): Int {
        return doneProperty.get(0)
    }

    fun attemptInsert(stack: ItemStack, slotRange: IntRange): ItemStack? {
        return moveStackToEmptySlot(stack, slotRange)
    }

    // Returns the moved itemstack, if it was moved. Else returns null. Does not do size/capacity checks
    fun moveStackToEmptySlot(stack: ItemStack, slotRange: IntRange): ItemStack? {
        for (slotIndex in slotRange) {
            val slot = slots[slotIndex]
            val slotStack = slot.item
            if (slotStack.isEmpty && slot.mayPlace(stack)) {
                val sizeToSet = stack.maxStackSize
                slot.setByPlayer(stack.split(stack.count.coerceAtMost(sizeToSet)))
                slot.setChanged()
                return slot.item
            }
        }
        return null
    }

    override fun quickMoveStack(pPlayer: Player, invSlot: Int): ItemStack {
        if (pPlayer is ServerPlayer) {
            val stack = getSlot(invSlot).item

            if (stack.item is BountyItem) {
                BountyStack(stack).setPickedUp(pPlayer.level().gameTime)
            }

            when (invSlot) {
                in BoardInventory.BOUNTY_RANGE -> {
                    val result = attemptInsert(stack, BoardInventory.HOTBAR_RANGE) ?: attemptInsert(stack, BoardInventory.INVENTORY_RANGE) ?: ItemStack.EMPTY
                    if (result != ItemStack.EMPTY) {
                        pPlayer.awardStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
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
                            return attemptInsert(stack, BoardInventory.DECREE_RANGE).also {
                                if (it != null) {
                                    pPlayer.currentBoardInteracting?.onUserPlacedDecree(pPlayer, it)
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
        checkContainerSize(container, BoardBlock.BOUNTY_SIZE)
        //this.inventory = inventory
        //some inventories do custom logic when a player opens it.
        container.startOpen(playerInventory.player)
        addDataSlots(doneProperty)

        val boardInv = container

        val bRows = 3
        val bCols = 7

        val bountySlotSize = 18
        val adjustX = 173
        val adjustY = 0

        // Bounties
        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                addSlot(BoardBountySlot(container, playerInventory.player, k + j * bCols, 8 + k * bountySlotSize + adjustX, 18 + j * bountySlotSize + adjustY))
            }
        }

        // Decrees
        for (j in 0 until 3) {
            addSlot(BoardDecreeSlot(boardInv, playerInventory.player, container.containerSize - 3 + j, 317, 18 + (j * 18)))
        }

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 181, 84)


        // Active Slot
        addSlot(BoardBountySlot(container, playerInventory.player, -1, 216 + 500000, 31))
    }
}

