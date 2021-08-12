package io.ejekta.bountiful.data.packets

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.api.network.IPacketInfo.Companion.dummy
import io.ejekta.kambrik.api.network.PacketInfo
import io.ejekta.kambrik.api.network.client.ClientMsg
import io.ejekta.kambrik.api.network.client.ClientMsgHandler
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

@Serializable
data class ClientUpdateBountySlot(val stacks: Map<Int, @Contextual ItemStack>) : ClientMsg<ClientUpdateBountySlot>(
    Handler
) {

    override fun onClientReceived(ctx: ClientMsgContext) {
        val gui = MinecraftClient.getInstance().currentScreen

        if ( gui is BoardScreen ) {
            val handler = gui.screenHandler as? BoardScreenHandler ?: return
            for ((slotNum, stack) in stacks) {
                handler.setStackInSlot(slotNum, stack)
            }
        }

    }

    companion object {
        val Handler = ClientMsgHandler(
            PacketInfo(Bountiful.id("doot") to { serializer() })
        )
    }
}