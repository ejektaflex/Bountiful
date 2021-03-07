@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag

val serMod = SerializersModule {
    polymorphic(Vehicle::class) {
        subclass(Car::class, Car.serializer())
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
val NbtFormatTest = NbtFormat {
    serializersModule = serMod
    writePolymorphic = true
}

val JsonTest = Json {
    serializersModule = serMod
}

@Serializable
abstract class Vehicle(val typed: String)

@Serializable
data class Car(val wheels: Int) : Vehicle("Automobile") {
    override fun toString(): String {
        return "Car[wheels=$wheels, typed=$typed]"
    }
}

@InternalSerializationApi
fun main(args: Array<String>) {
    val car: Vehicle = Car(4)

    val asJson = JsonTest.encodeToJsonElement(car)
    println("As Json: $asJson")

    val asNbt = NbtFormatTest.encodeToTag(car)
    println("As Nbt: $asNbt")

    val asObj = NbtFormatTest.decodeFromTag<Vehicle>(asNbt)
    println("As Obj: $asObj")



    /*
    // {"type":"io.ejekta.kambrikx.testing.Car","typed":"Automobile","wheels":4}
    val test = car
    println("AsJson: ${JsonTest.encodeToString(test)}")
    val asNbt = NbtFormatTest.encodeToTag(test)
    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0d,type:"Truck"}
    */



}


