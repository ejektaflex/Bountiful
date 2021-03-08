@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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

    @Serializable
    data class SlingshotData(val timesUsed: Int)
    val data = SlingshotData(0)

    val asNbt = NbtFormatTest.encodeToTag(data)
    println("As Nbt: $asNbt") // => {timesUsed:0}

    val asObj = NbtFormatTest.decodeFromTag<SlingshotData>(asNbt)
    println("As Obj: $asObj") // => SlingshotData(timesUsed=0)



    /*
    // {"type":"io.ejekta.kambrikx.testing.Car","typed":"Automobile","wheels":4}
    val test = car
    println("AsJson: ${JsonTest.encodeToString(test)}")
    val asNbt = NbtFormatTest.encodeToTag(test)
    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0d,type:"Truck"}
    */



}


