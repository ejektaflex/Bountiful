package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.TagClassEncoder
import io.ejekta.kambrikx.api.serial.nbt.TagEncoder
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonElement
import net.minecraft.nbt.Tag


@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {

    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Any {
        //lateinit var result: Tag
        //val encoder = TagEncoder { result = this }
        val encoder = TagEncoder()
        encoder.encodeSerializableValue(serializer, obj)
        //return result
        return encoder.root
    }

    @Serializable
    data class Wallet(val money: Double)

    @Serializable
    data class Person(val name: String, val age: Int, val items: Map<String, Int>)


    val b = encodeToTag(
        ListSerializer(Person.serializer()),
        listOf(
            Person("Bobby", 55, mapOf(
                "keys" to 23
            ))
        )
    )

    println("Result: $b")


}


