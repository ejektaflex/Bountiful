package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Serializable

@Serializable
class DecreePlaced : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Decree placed by: ${ctx.player}")
        BountifulContent.Triggers.DECREE_PLACED.trigger(ctx.player)
    }
}