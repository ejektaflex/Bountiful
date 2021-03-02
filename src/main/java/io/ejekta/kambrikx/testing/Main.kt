package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.TagEncoder
import kotlinx.serialization.*


@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {

    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Any {
        val encoder = TagEncoder()
        encoder.encodeSerializableValue(serializer, obj)
        return encoder.root
    }

    @Serializable
    data class Person(val name: String, val age: Int, val money: List<Int> = listOf())

    val b = encodeToTag(
        Person.serializer(),
        Person("Bobby", 35)
    )

    println("Result: $b")


}


