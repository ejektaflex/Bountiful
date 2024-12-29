package io.ejekta.bountiful.messages

import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.util.ctx
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class UpdateCriteriaObjective(val slot: Int, val objId: String) : KambrikMsg() {
    override fun onClientReceived() {
        println("Client received update bounty tooltip update with slot number: $slot")
        val player = ctx.player

        if (player == null) {
            println("Player was null, can't update the tooltip!!")
        } else {
            val stack = player.inventory.getItem(slot)

            if (stack.item is BountyItem) {
                // Update completion of that criteria on the obj tracker
                BountyStack(stack).advance(objId)
            }
        }
    }
}