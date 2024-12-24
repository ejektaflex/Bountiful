@file:Suppress("OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS")

package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.gui.screen.KambrikContainerMenu
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack


class AnalyzerScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: Inventory,
    override var container: SimpleContainer
) : KambrikContainerMenu<AnalyzerScreenHandler, SimpleContainer>(BountifulContent.ANALYZER_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: Inventory) : this(syncId, playerInventory,
        SimpleContainer(SIZE)
    )

    override fun stillValid(player: Player): Boolean {
        return container.stillValid(player)
    }

    override fun removed(player: Player) {
        // return slot item to player, right? We don't want them to lose these items forever.
        val leftover = container.removeItemNoUpdate(0)
        player.inventory.placeItemBackInInventory(leftover)
        container.stopOpen(player)
        super.removed(player)
    }

    override fun quickMoveStack(pPlayer: Player, invSlot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    init {
        checkContainerSize(container, SIZE)
        container.startOpen(playerInventory.player)

        addSlot(AnalyzerDecreeSlot(container,  playerInventory.player, 0, 153, 18))

        //The player inventory
        makePlayerDefaultGrid(playerInventory, 9, 85)

    }

    companion object {
        const val SIZE = 1
    }
}

