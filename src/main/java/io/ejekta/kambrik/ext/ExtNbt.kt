package io.ejekta.kambrik.ext

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag

operator fun CompoundTag.iterator(): Iterator<Pair<String, Tag>> {
    return keys.map { it to get(it)!! }.iterator()
}

fun StringNbtReader.parseTag(nbt: String): Tag {
    return StringNbtReader.parse("{dummy:$nbt}")
}

fun String.toTag(): Tag {
    return StringNbtReader.parse("{dummy:$this}").get("dummy")!!
}

