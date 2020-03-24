package ejektaflex.bountiful.network

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

interface IPacketMessage {

    fun encode(buff: PacketBuffer)

    fun decode(buff: PacketBuffer)

    fun execute()

    /**
     * Handles the given packet message. By default, calls [execute] on the main thread.
     */
    fun handle(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().apply {
            enqueueWork {
                execute()
            }
            packetHandled = true
        }
    }

}