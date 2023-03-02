package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable

@Serializable
class ClipboardCopy(val text: String) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        Bountiful.LOGGER.info("Copying text to clipboard: $text")
        ctx.client.keyboard.clipboard = text
    }
}