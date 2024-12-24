package io.ejekta.bountiful.messages

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

@Serializable
class ClientPlayerStatus(private val statusType: Type) : KambrikMsg() {

    override fun onClientReceived() {
        statusType.msgFunc()
    }

    enum class Type(val msgFunc: () -> Unit) {
        UPDATE_ANALYZER({
            val player = Minecraft.getInstance().player
            Bountiful.LOGGER.info("Analyzer request received by: $player")
            val analyzerScreen = (Minecraft.getInstance().screen as? AnalyzerScreen)

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
}