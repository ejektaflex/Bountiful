package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.messages.ServerPlayerStatus
import io.ejekta.kambrik.bridge.Kambridge
import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class BoardDecreeSlot(inv: BoardInventory, val usingPlayer: PlayerEntity, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun canInsert(stack: ItemStack?) = stack?.item == BountifulContent.DECREE_ITEM

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        return inventory.count(BountifulContent.DECREE_ITEM) > 1
    }

    override fun insertStack(stack: ItemStack?, count: Int): ItemStack {
        if (Kambridge.isOnClient() && usingPlayer.server == null && stack?.item is DecreeItem && count >= 1) {
            ServerPlayerStatus.Type.DECREE_PLACED.sendToServer()
        }
        return super.insertStack(stack, count)
    }
}