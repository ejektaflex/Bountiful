
package ejektaflex.bountiful.network

import ejektaflex.bountiful.BountifulMod
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Suppress("INACCESSIBLE_TYPE")
object BountifulNetwork {

    private var msgId: Int = 0

    val channel = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(BountifulMod.MODID, "main"))
            .networkProtocolVersion { BountifulMod.VERSION }
            .clientAcceptedVersions { true }
            .serverAcceptedVersions { true }
            .simpleChannel()


    private fun <M : IPacketMessage> registerMessage(clazz: KClass<M>) {

        channel.registerMessage(
                msgId++,
                clazz.java,
                { msg: M, buff: PacketBuffer -> msg.encode(buff) },
                { buff: PacketBuffer -> clazz.createInstance().apply { decode(buff) }},
                { msg: M, buff: Supplier<NetworkEvent.Context> -> msg.handle(buff) }
        )

    }

    fun register() {
        registerMessage(MessageClipboardCopy::class)
    }

}