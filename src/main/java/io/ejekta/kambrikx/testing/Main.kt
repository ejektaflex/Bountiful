@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.testing

import io.ejekta.kambrik.ext.toTag
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import io.ejekta.kambrikx.api.serial.serializers.BlockPosSerializer
import io.ejekta.kambrikx.api.serial.serializers.TagSerializer
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.minecraft.client.realms.util.JsonUtils
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos

val serMod = SerializersModule {

    polymorphic(Vehicle::class) {
        subclass(Car::class, Car.serializer())
    }

    polymorphic(Money::class) {
        subclass(Wallet::class, Wallet.serializer())
    }

    include(NbtFormat.BuiltInSerializers)

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
    data class SlingshotData(val name: String, @Contextual val tag: ListTag)
    val data = SlingshotData("Bob", ListTag().apply {
        add(LongArrayTag(longArrayOf(4L, 6L, 8L)))
    })

    val asNbt = NbtFormatTest.encodeToTag(data)
    println("As Nbt: $asNbt") // => {name:"Bob",tag:'{hello:"there"}'}

    val asObj = NbtFormatTest.decodeFromTag<SlingshotData>(asNbt)
    println("As Obj: $asObj") // => SlingshotData(name=Bob, tag={hello:"there"})



    /*
    // {"type":"io.ejekta.kambrikx.testing.Car","typed":"Automobile","wheels":4}
    val test = car
    println("AsJson: ${JsonTest.encodeToString(test)}")
    val asNbt = NbtFormatTest.encodeToTag(test)
    println("Result: $asNbt") // => Result: {tirePressure:[28.1d,32.0d,31.5d,34.6d],gas:100.0d,type:"Truck"}
    */



}


