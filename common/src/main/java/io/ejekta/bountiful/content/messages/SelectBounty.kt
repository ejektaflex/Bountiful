package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Serializable

@Serializable
class SelectBounty(private val index: Int, private val uuidString: String) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        val handler = ctx.player.server.playerManager.playerList.firstOrNull {
            it.uuidAsString == uuidString
        }?.currentScreenHandler as? BoardScreenHandler ?: return
        handler.inventory.select(index)
    }
}