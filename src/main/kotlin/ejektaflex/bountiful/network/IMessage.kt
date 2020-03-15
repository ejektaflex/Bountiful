package ejektaflex.bountiful.network

import net.minecraft.network.PacketBuffer

interface IMessage {
    fun encode(buff: PacketBuffer)
    fun decode(buff: PacketBuffer)
}