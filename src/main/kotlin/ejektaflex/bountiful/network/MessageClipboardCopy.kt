package ejektaflex.bountiful.network

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class MessageClipboardCopy(var copyText: String = "INVALID") : IMessage {

    override fun decode(buff: PacketBuffer) {
        copyText = buff.readString()
    }

    override fun encode(buff: PacketBuffer) {
        buff.writeString(copyText)
    }


    companion object Handler : IMessageHandler<MessageClipboardCopy> {
        override fun handle(msg: MessageClipboardCopy, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                Minecraft.getInstance().keyboardListener.clipboardString = msg.copyText
            }
        }
    }

}