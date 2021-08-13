package io.ejekta.bountiful.data.packets

import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.api.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

@Serializable
data class ClientUpdateBountySlot(val slotNum: Int, val nbt: @Contextual NbtCompound? = null) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {

        println("Got client update for bounty slot! It's on slot $slotNum")

        val gui = MinecraftClient.getInstance().currentScreen

        if ( gui is BoardScreen ) {
            val handler = gui.screenHandler as? BoardScreenHandler ?: return
            if (nbt != null) {
                handler.setStackInSlot(slotNum, 1, ItemStack.fromNbt(nbt))
            } else {
                handler.setStackInSlot(slotNum, 1, ItemStack.EMPTY)
            }
        }

    }

}