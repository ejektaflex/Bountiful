package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable

@Serializable
class UpdateBountyCriteriaObjective(val slot: Int, val objIndex: Int) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Client received update bounty tooltip update with slot number: $slot")
        val player = ctx.client.player

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