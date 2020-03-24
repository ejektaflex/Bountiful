package ejektaflex.bountiful.network

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer

class MessageClipboardCopy(var copyText: String = "INVALID") : IPacketMessage {

    override fun decode(buff: PacketBuffer) {
        copyText = buff.readString()
    }

    override fun encode(buff: PacketBuffer) {
        buff.writeString(copyText)
    }

    override fun execute() {
        Minecraft.getInstance().keyboardListener.clipboardString = copyText
    }

}