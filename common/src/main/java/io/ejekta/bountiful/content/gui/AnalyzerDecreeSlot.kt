package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.messages.ClientPlayerStatus
import io.ejekta.bountiful.util.currentBoardInteracting
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class AnalyzerDecreeSlot(inv: SimpleInventory, val usingPlayer: PlayerEntity, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?) = stack?.item == BountifulContent.DECREE_ITEM

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        return true
    }

    override fun setStack(stack: ItemStack?) {
        super.setStack(stack)
        stack?.let {
            if (Kambridge.isOnClient()) {
                val anScreen = MinecraftClient.getInstance().currentScreen as? AnalyzerScreen
                anScreen?.refreshWidgets()
            }
        }
    }
}