package io.ejekta.bountiful.data.packets

import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.api.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

@Serializable
data class ClientUpdateBountySlot(val stacks: Map<Int, @Contextual ItemStack>) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {

        println("Got client update for bounty slot!")

        val gui = MinecraftClient.getInstance().currentScreen

        if ( gui is BoardScreen ) {
            val handler = gui.screenHandler as? BoardScreenHandler ?: return
            for ((slotNum, stack) in stacks) {
                // TODO figure out what revision is
                handler.setStackInSlot(slotNum, 1, stack)
            }
        }

    }

}