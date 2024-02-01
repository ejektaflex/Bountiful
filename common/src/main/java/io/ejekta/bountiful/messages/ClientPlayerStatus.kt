package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
class ClientPlayerStatus(private val statusType: Type) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {
        statusType.msgFunc(ctx)
    }

    enum class Type(val msgFunc: MsgContext.() -> Unit) {
        UPDATE_ANALYZER({
            val player = client.player
            Bountiful.LOGGER.info("Analyzer request received by: $player")
            val analyzerScreen = (client.currentScreen as? AnalyzerScreen)

            analyzerScreen?.let {
                println("Got analyzer")
                it.refreshWidgets()
            }
        })
        ;

        fun sendToClient(player: ServerPlayerEntity) {
            println("Sending $this to server..")
            ClientPlayerStatus(this).sendToClient(player)
        }
    }
}