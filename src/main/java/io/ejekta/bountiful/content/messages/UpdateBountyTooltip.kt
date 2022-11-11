package io.ejekta.bountiful.content.messages

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.nbt.NbtCompound

@Serializable
class UpdateBountyTooltip(val slot: Int, val compound: @Contextual NbtCompound) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Client received update bounty tooltip update with slot number: $slot")
        val player = ctx.client.player

        if (player == null) {
            println("Player was null, can't update the tooltip!!")
        } else {
            val stack = player.inventory.getStack(slot)

            if (stack.item is BountyItem) {
                println("Lets do it bro")

                val payload = compound.get("payload") ?: return
                println("Doing..")
                val newData = BountyData.decode(payload)
                println("New data is: $newData")
                BountyData[stack] = newData
                BountyInfo[stack] = BountyInfo[stack].update(newData, player.world.time)
            }
        }
    }
}