package io.ejekta.bountiful.content.messages

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable

@Serializable
class ClipboardCopy(val text: String) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        ctx.client.keyboard.clipboard = text
    }
}