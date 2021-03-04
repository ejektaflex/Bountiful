@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@ExperimentalSerializationApi
val NbtFormatTest = NbtFormat {
    serializersModule = SerializersModule {
        polymorphic(Vehicle::class) {
            subclass(Car::class, Car.serializer())
        }
    }
}

@Serializable
abstract class Vehicle(val type: String)

@Serializable
class Car(val wheels: Int) : Vehicle("Automobile")

@InternalSerializationApi
fun main(args: Array<String>) {
    val car: Vehicle = Car(4)
    val asNbt = NbtFormatTest.encodeToTag(car)

    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0d,type:"Truck"}
}


