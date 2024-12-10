package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayer

@Serializable
class ClientPlayerStatus(private val statusType: Type) : KambrikMsg() {

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

        fun sendToClient(player: ServerPlayer) {
            Bountiful.LOGGER.debug("Sending $this to server..")
            ClientPlayerStatus(this).sendToClient(player)
        }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    companion object {
        val ID = CustomPayload.id<ClientPlayerStatus>("client_player_status")
    }
}