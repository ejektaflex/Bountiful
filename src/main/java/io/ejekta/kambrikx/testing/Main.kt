@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import io.ejekta.kambrikx.api.serial.serializers.StringTagSerializer
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

val serMod = SerializersModule {
    polymorphic(Vehicle::class) {
        subclass(Car::class, Car.serializer())
    }

    //include(NbtFormat.BuiltInSerializers)


    polymorphic(Money::class) {
        subclass(Wallet::class, Wallet.serializer())
    }

    //*
    polymorphic(Tag::class) {
        subclass(StringTag::class, StringTagSerializer)
    }

     //*/

    contextual(StringTag::class, StringTagSerializer)

}

@InternalSerializationApi
@ExperimentalSerializationApi
val NbtFormatTest = NbtFormat {
    serializersModule = serMod
    writePolymorphic = true
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
    data class SlingshotData(@Contextual val tag: StringTag)
    val data = SlingshotData(StringTag.of("Hello!"))

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


