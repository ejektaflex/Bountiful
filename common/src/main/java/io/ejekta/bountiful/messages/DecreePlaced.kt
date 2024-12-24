package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class DecreePlaced : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Decree placed by: ${ctx.player}")
        BountifulContent.Triggers.DECREE_PLACED.trigger(ctx.player)
    }
}