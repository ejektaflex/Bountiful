package io.ejekta.kambrikx.testing

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.*



@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {

    @Serializable
    data class Wallet(val money: Double)

    @Serializable
    open class Person(val name: String, val age: Int)

    class OldJohn : Person("John", 77)

    val format = NbtFormat {
        
    }

    val b = format.encodeToTag(
        //Person("Dotty", 36)
        OldJohn()
    )

    println("Result: $b")


}


