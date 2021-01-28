package io.ejekta.bountiful.common.util

import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.json.*
import net.minecraft.nbt.CompoundTag

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

    println("NBT: $nbt")

    println("JSON: $jsonish")

    println(jsonish.toTag())



}