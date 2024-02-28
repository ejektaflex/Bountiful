package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient

@Serializable
class ClipboardCopy(val text: String) : ClientMsg() {
    override fun onClientReceived() {
        Bountiful.LOGGER.info("Copying text to clipboard: $text")
        MinecraftClient.getInstance().keyboard.clipboard = text
    }
}