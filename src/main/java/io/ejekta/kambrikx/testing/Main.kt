package io.ejekta.kambrikx.testing

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.api.nbt.NbtMode
import io.ejekta.kambrikx.ext.toLenientJson
import io.ejekta.kambrikx.ext.toLenientTag
import io.ejekta.kambrikx.api.nbt.TagConverter
import io.ejekta.kambrikx.api.nbt.TagConverterLenient
import io.ejekta.kambrikx.ext.NBT
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.nbt.*

fun main(args: Array<String>) {

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




    @Serializable
    data class DamageCounter(val name: String, val damage: Float)

    val asNbt = Kambrik.NBT.toNbt(DamageCounter("Test Counter", 5f), mode = NbtMode.STRICT)
    println(asNbt) // => {damage:5.0d,name:"Test Counter"}

    val asObjAgain = Kambrik.NBT.fromNbt<DamageCounter>( DamageCounter.serializer(), asNbt, mode = NbtMode.STRICT)
    println(asObjAgain) // => DamageCounter(name=Test Counter, damage=5.0)

    /*
    val ser = Json.encodeToJsonElement(DamageCounter.serializer(), DamageCounter("Test", 1.2f)).toLenientTag()

    println("Ser: $ser")

    val jso = ser.toLenientJson()

    println("Jso: $jso")

    val deser = Json.decodeFromJsonElement(DamageCounter.serializer(), jso)

    println("Deser: $deser")
    */









}