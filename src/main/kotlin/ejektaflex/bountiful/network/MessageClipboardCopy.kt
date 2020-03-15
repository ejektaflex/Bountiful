package ejektaflex.bountiful.network

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class MessageClipboardCopy(var copyText: String = "INVALID") {

    constructor(buf: PacketBuffer) : this() {
        copyText = buf.readString()
    }

    fun encode(buf: PacketBuffer) {
        buf.writeString(copyText)
    }


    companion object Handler {
        fun handle(msg: MessageClipboardCopy, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                Minecraft.getInstance().keyboardListener.clipboardString = msg.copyText
            }
        }
    }

}