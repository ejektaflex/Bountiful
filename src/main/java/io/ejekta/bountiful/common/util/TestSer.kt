package io.ejekta.bountiful.common.util

import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

fun main() {

    val nbt = CompoundTag().apply {
        putString("hello", "there")
    }

    val jsonish = buildJsonObject {
        put("hi", "sir")
        putJsonArray("alist") {
            add("of")
            add("stuff")
            add(4)
        }
    }


    val what2 = CompoundTag().apply {
        putLongArray("hi", listOf(1L, 2L, 3L))
        put("derp", TagFreeMarker)
        put("doot", ListTag().apply {
            this.add(StringTag.of("hullo"))
        })
    }

    println("WHAT2: $what2")

    println("Byte of list1: ${what2.getList("doot",-37)}")
    println("Byte of list2: ${what2.getList("doot",44)}")

    println("NBT: $nbt")

    println("JSON: $jsonish")

    println(jsonish.toTag())



}