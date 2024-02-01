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
        // return slot item to player, right? We don't want them to lose these items forever.
        val leftover = inventory.removeStack(0)
        player.inventory.insertStack(leftover)
        inventory.onClose(player)
    }

    override fun quickMove(player: PlayerEntity, invSlot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    init {
        checkSize(inventory, SIZE)
        inventory.onOpen(playerInventory.player)

        addSlot(AnalyzerDecreeSlot(inventory,  playerInventory.player, 0, 153, 18))

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 9, 85)

    }

    companion object {
        const val SIZE = 1
    }
}

