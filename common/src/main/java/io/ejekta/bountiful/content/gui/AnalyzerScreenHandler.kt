@file:Suppress("OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS")

package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.gui.screen.KambrikScreenHandler
import net.minecraft.entity.player.Player
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack


class AnalyzerScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: SimpleInventory
) : KambrikScreenHandler<AnalyzerScreenHandler, SimpleInventory>(BountifulContent.ANALYZER_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory,
        SimpleInventory(SIZE)
    )

    override fun canUse(player: Player): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun onClosed(player: Player) {
        // return slot item to player, right? We don't want them to lose these items forever.
        val leftover = inventory.removeStack(0)
        player.inventory.offerOrDrop(leftover)
        inventory.onClose(player)
        super.onClosed(player)
    }

    override fun quickMove(player: Player, invSlot: Int): ItemStack {
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

