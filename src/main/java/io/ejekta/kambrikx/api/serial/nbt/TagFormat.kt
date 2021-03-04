package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.modules.EmptySerializersModule

@ExperimentalSerializationApi
object TagFormat : SerialFormat {

    override val serializersModule = EmptySerializersModule

    /*
    @InternalSerializationApi
    fun <T : Any> decodeFromNbt(strategy: DeserializationStrategy<T>, item: Tag) {
        TagDecoder()
    }

     */


}