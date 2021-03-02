package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.Tag

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