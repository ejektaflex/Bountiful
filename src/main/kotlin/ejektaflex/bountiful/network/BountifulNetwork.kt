
package ejektaflex.bountiful.network

import ejektaflex.bountiful.BountifulMod
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

@Suppress("INACCESSIBLE_TYPE")
object BountifulNetwork {


    private var msgId: Int = 0

    val channel = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(BountifulMod.MODID, "network"))
            .networkProtocolVersion { BountifulMod.VERSION }
            .clientAcceptedVersions { true }
            .serverAcceptedVersions { true }
            .simpleChannel()




    fun <M : IMessage, H : IMessageHandler<M>> registerMessage(clazz: KClass<M>, handler: H) {

        channel.registerMessage(
                msgId++,
                clazz.java,
                { t: M, u: PacketBuffer -> t.encode(u) },
                { t: PacketBuffer -> clazz.createInstance().apply { decode(t) }},
                handler::handle
        )

    }

    fun register() {
        registerMessage(MessageClipboardCopy::class, MessageClipboardCopy.Handler)
    }

}