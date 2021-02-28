package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.serial.TagType
import io.ejekta.kambrikx.serial.convert.BoxedTag
import io.ejekta.kambrikx.serial.convert.TagConverter
import io.ejekta.kambrikx.serial.convert.TagConverterLenient
import io.ejekta.kambrikx.serial.convert.TagConverterStrict
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*

fun main(args: Array<String>) {

    /*
    val converter = listOf(
            TagConverterLenient,
            TagConverterStrict
    )[1]

    //val testTag = LongArrayTag(longArrayOf(1L, 2L, 9223372036854775807))
    val testTag = CompoundTag().apply {
        putBoolean("bool", true)
        putInt("num", 5)
    }

    val jsonified = converter.toJson(testTag)

    println(testTag)
    println(jsonified)

    val backported = converter.toTag(jsonified)

    println(backported)

     */

    val converter: TagConverter = TagConverterLenient

    //val orig = LongArrayTag(longArrayOf(1L, 2L, 1337L))
    val orig = CompoundTag().apply {
        putString("Hello", "World!")
        putInt("IntTest", 100)
        putDouble("DblTest", 110.5)
        putBoolean("BlnTest", true)
        putFloat("FltTest", 1.2f)
        putLongArray("LongItems", longArrayOf(1L, 2L, 100L))
        putByteArray("ByteItems", byteArrayOf(1, 2, 32))
        putIntArray("IntrItems", intArrayOf(1, 2, 64))
    }

    println("Original: \t$orig")

    val bt = converter.toJson(orig)
    println("Convrt'd: \t$bt")

    val be = converter.toTag(bt)
    println("Back2NBT: \t$be")

    println("Equal: ${orig.toString() == be.toString()}")



}