package io.ejekta.bountiful.data.messages

import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.kambrik.api.message.ServerMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
class ServerMaskBountySlot(val pos: @Contextual BlockPos, private val slotNum: Int) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Adding server mask !")
        val board = ctx.player.world.getBlockEntity(pos) as? BoardBlockEntity ?: return
        board.addToMask(ctx.player, slotNum)
    }
}
