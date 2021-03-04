@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@ExperimentalSerializationApi
val NbtFormatTest = NbtFormat {
    writePolymorphic = false
}

@Serializable
class Vehicle(val type: String, val gas: Float, val tirePressure: List<Double>)

@InternalSerializationApi
fun main(args: Array<String>) {
    val truck = Vehicle("Truck", 100f, mutableListOf(28.1, 32.0, 31.5, 34.6))
    val asNbt = encodeToTag(truck)

    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0f,type:"Truck"}
}


