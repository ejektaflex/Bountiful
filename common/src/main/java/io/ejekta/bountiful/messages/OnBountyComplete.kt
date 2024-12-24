package io.ejekta.bountiful.messages

import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.ctx
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.player.Player

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
        if (ctx.screen == null && BountifulIO.configData.client.showCompletionToast) {
            player.playSound(soundEvent, volume, pitch)

            ctx.toasts.addToast(
                SystemToast(
                    SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                    Component.translatable("bounty.toast.complete"), // Bounty Complete!
                    Component.translatable("bounty.toast.complete.desc") // Turn in at a Bounty Board!
                    )
            )
        }
    }
}