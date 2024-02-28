package io.ejekta.bountiful.messages

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.util.ctx
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient

@Serializable
class UpdateBountyCriteriaObjective(val slot: Int, val objIndex: Int) : ClientMsg() {
    override fun onClientReceived() {
        println("Client received update bounty tooltip update with slot number: $slot")
        val player = ctx.player

        if (player == null) {
            println("Player was null, can't update the tooltip!!")
        } else {
            val stack = player.inventory.getStack(slot)

            if (stack.item is BountyItem) {

                BountyData.edit(stack) {
                    objectives[objIndex].current += 1
                }

            }
        }
    }
}