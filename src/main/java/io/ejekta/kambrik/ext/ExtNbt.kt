package io.ejekta.kambrik.ext

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import net.minecraft.network.PacketByteBuf

operator fun CompoundTag.iterator(): Iterator<Pair<String, Tag>> {
    return keys.map { it to get(it)!! }.iterator()
}

fun StringNbtReader.parseTag(nbt: String): Tag {
    return StringNbtReader.parse("{content:$nbt}")
}

fun String.toTag(): Tag {
    return StringNbtReader.parse("{content:$this}").get("content")!!
}

fun Tag.wrapToPacketByteBuf(): PacketByteBuf {
    return PacketByteBufs.create().apply {
        writeCompoundTag(CompoundTag().apply {
            put("content", this@wrapToPacketByteBuf.copy())
        })
    }
}

fun PacketByteBuf.unwrapToTag(): Tag {
    return readCompoundTag()!!.get("content")!!
}

