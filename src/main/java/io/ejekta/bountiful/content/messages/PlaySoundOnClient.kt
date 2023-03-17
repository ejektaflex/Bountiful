package io.ejekta.bountiful.content.messages

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvent

@Serializable
class PlaySoundOnClient(
    private val soundEvent: @Contextual SoundEvent,
    private val volume: Float,
    private val pitch: Float
) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        runLocally(ctx.client.player!!)
    }

    fun runLocally(player: PlayerEntity) {
        player.playSound(soundEvent, volume, pitch)
    }
}