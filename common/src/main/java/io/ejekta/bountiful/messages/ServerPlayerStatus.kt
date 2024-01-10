package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.bountiful.content.BountifulTriggers
import kotlinx.serialization.Serializable

@Serializable
class ServerPlayerStatus(private val statusType: Type) : ServerMsg() {

    override fun onServerReceived(ctx: MsgContext) {
        statusType.msgFunc(ctx)
    }

    enum class Type(val msgFunc: MsgContext.() -> Unit) {
        DECREE_PLACED({
            println("Decree placed by: $player")
            BountifulTriggers.DECREE_PLACED.trigger(player)
        }),
        BOUNTY_TAKEN({
            println("Incrementing bounty taken stat!")
            player.incrementStat(BountifulContent.CustomStats.BOUNTIES_TAKEN)
        })
        ;

        fun sendToServer() {
            println("Sending $this to server..")
            ServerPlayerStatus(this).sendToServer()
        }
    }
}