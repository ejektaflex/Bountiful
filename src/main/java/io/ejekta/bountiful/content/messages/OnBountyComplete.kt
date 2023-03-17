package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text

@Serializable
class OnBountyComplete(
    private val soundEvent: @Contextual SoundEvent,
    private val volume: Float,
    private val pitch: Float
) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        runLocally(ctx.client.player!!)
    }

    fun runLocally(player: PlayerEntity) {
        val mc = MinecraftClient.getInstance()

        // Don't show toasts when in an inventory (to prevent toast spam when moving items related to bounties)
        if (mc.currentScreen == null && BountifulIO.configData.showCompletionToast) {
            player.playSound(soundEvent, volume, pitch)
            mc.toastManager.add(
                SystemToast.create(
                    mc,
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Text.literal("Bounty Complete!"),
                    Text.literal("Turn in at a Bounty Board!")
                )
            )
        }

    }
}