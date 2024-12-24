package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

@Serializable
class ClipboardCopy(val text: String) : KambrikMsg() {
    override fun onClientReceived() {
        Bountiful.LOGGER.info("Copying text to clipboard: $text")
        Minecraft.getInstance().keyboardHandler.clipboard = text
    }
}