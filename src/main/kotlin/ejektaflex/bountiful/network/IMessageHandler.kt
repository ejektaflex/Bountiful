package ejektaflex.bountiful.network

import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

interface IMessageHandler<T : IMessage> {
    fun handle(msg: T, ctx: Supplier<NetworkEvent.Context>)
}