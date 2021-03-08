@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.*
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import net.minecraft.nbt.CompoundTag

val serMod = SerializersModule {
    polymorphic(Vehicle::class) {
        subclass(Car::class, Car.serializer())
    }
    polymorphic(Money::class) {
        subclass(Wallet::class, Wallet.serializer())
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

interface Money

@Serializable
data class Wallet(val amount: Double) : Money

@Serializable
data class Person(val name: String, val money: Money)


@InternalSerializationApi
fun main(args: Array<String>) {

    val test: Pair<String, Vehicle> = "Hello" to Car(5)
    //val test = Person("Bob", Wallet(100.0))

    val asJson = JsonTest.encodeToJsonElement(test)
    println("As Json: $asJson")

    val asJsonObj = JsonTest.decodeFromJsonElement<Person>(asJson)
    println("As Test Again: $asJsonObj")

    val asNbt = NbtFormatTest.encodeToTag(test)
    println("As Nbt: $asNbt")

    val asObj = NbtFormatTest.decodeFromTag<Person>(asNbt)
    println("As Obj: $asObj")



    /*
    // {"type":"io.ejekta.kambrikx.testing.Car","typed":"Automobile","wheels":4}
    val test = car
    println("AsJson: ${JsonTest.encodeToString(test)}")
    val asNbt = NbtFormatTest.encodeToTag(test)
    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0d,type:"Truck"}
    */



}


