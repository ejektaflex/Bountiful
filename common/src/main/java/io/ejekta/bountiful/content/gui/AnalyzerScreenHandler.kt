@file:Suppress("OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS")

package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.kambrik.gui.screen.KambrikScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos


class AnalyzerScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: SimpleInventory
) : KambrikScreenHandler<AnalyzerScreenHandler, SimpleInventory>(BountifulContent.ANALYZER_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory,
        SimpleInventory(SIZE)
    )

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun onClosed(player: PlayerEntity) {
        // TODO return slots to player, right? We don't want them to lose these items forever.
        inventory.onClose(player)
    }

    override fun quickMove(player: PlayerEntity, invSlot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    init {
        checkSize(inventory, SIZE)
        inventory.onOpen(playerInventory.player)

        val bRows = 1
        val bCols = SIZE

        val slotSize = 18
        val adjustX = 173
        val adjustY = 0

        // Bounties
        for (j in 0 until bRows) {
            for (k in 0 until bCols) {
                addSlot(Slot(inventory, k + j * bCols, 8 + k * slotSize + adjustX, 18 + j * slotSize + adjustY))
            }
        }

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 9, 85)

    }

    companion object {
        const val SIZE = 6
    }
}

