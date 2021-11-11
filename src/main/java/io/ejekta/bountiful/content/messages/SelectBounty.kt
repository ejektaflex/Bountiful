package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Serializable

@Serializable
class SelectBounty(val index: Int) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        val handler = ctx.server.playerManager.playerList.first().currentScreenHandler as? BoardScreenHandler ?: return
        handler.inventory.select(index)
    }
}