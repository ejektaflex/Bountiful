package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json


@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {

    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Any {
        val encoder = TagEncoder()
        encoder.encodeSerializableValue(serializer, obj)
        return encoder.root
    }

    @Serializable
    data class Wallet(val money: Double)

    @Serializable
    data class Person(val name: String, val age: Int)

    val b = encodeToTag(
        Person.serializer(),
        Person("Dotty", 36)
    )

    println("Result: $b")


}


