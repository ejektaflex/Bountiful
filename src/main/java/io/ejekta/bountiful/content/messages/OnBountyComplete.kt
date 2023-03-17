package io.ejekta.bountiful.content.messages

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
        player.playSound(soundEvent, volume, pitch)

        MinecraftClient.getInstance().toastManager.add(
            SystemToast.create(
                MinecraftClient.getInstance(),
                SystemToast.Type.PERIODIC_NOTIFICATION,
                Text.literal("Hello!"),
                Text.literal("This is a description!").append(
                    Text.literal("\nHallo!")
                )
            )
        )
    }
}