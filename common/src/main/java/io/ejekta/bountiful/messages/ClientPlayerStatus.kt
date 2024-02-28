package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
class ClientPlayerStatus(private val statusType: Type) : ClientMsg() {

    override fun onClientReceived() {
        statusType.msgFunc()
    }

    enum class Type(val msgFunc: () -> Unit) {
        UPDATE_ANALYZER({
            val player = MinecraftClient.getInstance().player
            Bountiful.LOGGER.info("Analyzer request received by: $player")
            val analyzerScreen = (MinecraftClient.getInstance().currentScreen as? AnalyzerScreen)

            analyzerScreen?.let {
                println("Got analyzer")
                it.refreshWidgets()
            }
        })
        ;

        fun sendToClient(player: ServerPlayerEntity) {
            Bountiful.LOGGER.debug("Sending $this to server..")
            ClientPlayerStatus(this).sendToClient(player)
        }
    }
}