package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Serializable
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
class ClientPlayerStatus(private val statusType: Type) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {
        statusType.msgFunc(ctx)
    }

    enum class Type(val msgFunc: MsgContext.() -> Unit) {
        OPEN_ANALYZER({
            val player = client.player
            Bountiful.LOGGER.info("Analyzer request received by: $player")

        })
        ;

        fun sendToClient(player: ServerPlayerEntity) {
            println("Sending $this to server..")
            ClientPlayerStatus(this).sendToClient(player)
        }
    }
}