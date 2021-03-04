package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.encodeToTag
import kotlinx.serialization.*



@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {

    @Serializable
    data class Wallet(val money: Double)

    @Serializable
    data class Person(val name: String, val age: Int)

    val b = encodeToTag(
        Person("Dotty", 36)
    )

    println("Result: $b")


}


