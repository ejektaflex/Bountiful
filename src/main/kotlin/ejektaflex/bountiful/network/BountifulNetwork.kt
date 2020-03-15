
package ejektaflex.bountiful.network

import ejektaflex.bountiful.BountifulMod
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.IndexedMessageCodec
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.function.BiConsumer
import java.util.function.Supplier

object BountifulNetwork {


    private var msgId: Int = 0

    val channel = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(BountifulMod.MODID, "network"))
            .networkProtocolVersion { BountifulMod.version }
            .clientAcceptedVersions { true }
            .serverAcceptedVersions { true }
            .simpleChannel()




    fun register() {


        channel.registerMessage(
                msgId++,
                MessageClipboardCopy::class.java,
                MessageClipboardCopy::encode,
                ::MessageClipboardCopy,
                MessageClipboardCopy.Handler::handle
        )




        //Doot.Doot()


    }




}