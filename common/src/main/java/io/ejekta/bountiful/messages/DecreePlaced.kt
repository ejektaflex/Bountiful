package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.BountifulTriggers
import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Serializable

@Serializable
class DecreePlaced : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Decree placed by: ${ctx.player}")
        BountifulTriggers.DECREE_PLACED.trigger(ctx.player)
    }
}