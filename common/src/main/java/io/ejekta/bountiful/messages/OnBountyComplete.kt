package io.ejekta.bountiful.messages

import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.ctx
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.toast.SystemToast
import net.minecraft.entity.player.Player
import net.minecraft.network.packet.CustomPayload
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text

@Serializable
data class OnBountyComplete(
    private val soundEvent: @Contextual SoundEvent,
    private val volume: Float,
    private val pitch: Float
) : KambrikMsg() {
    override fun onClientReceived() {
        runLocally(ctx.player!!)
    }

    fun runLocally(player: Player) {
        // Don't show toasts when in an inventory (to prevent toast spam when moving items related to bounties)
        if (ctx.currentScreen == null && BountifulIO.configData.client.showCompletionToast) {
            player.playSound(soundEvent, volume, pitch)
            ctx.toastManager.add(
                SystemToast.create(
                    ctx,
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Component.literal("Bounty Complete!"),
                    Component.literal("Turn in at a Bounty Board!")
                )
            )
        }
    }

    override fun getId(): CustomPayload.Id<OnBountyComplete> = ID

    companion object {
        val ID = CustomPayload.id<OnBountyComplete>("bounty_complete")
    }
}