package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class SelectBounty(private val index: Int, private val uuidString: String) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        val handler = ctx.player.server.playerList.players.firstOrNull {
            it.stringUUID == uuidString
        }?.containerMenu as? BoardScreenHandler ?: return
        handler.container.select(index)
    }
}