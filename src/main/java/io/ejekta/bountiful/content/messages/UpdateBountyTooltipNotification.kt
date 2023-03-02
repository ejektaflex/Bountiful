package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.nbt.NbtCompound

@Serializable
class UpdateBountyTooltipNotification(val slot: Int, val compound: @Contextual NbtCompound) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {

    }
}