package io.ejekta.kambrikx.api.serial.nbt

import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

class NbtFormatConfig {

    var classDiscriminator: String = "type"

    @ExperimentalSerializationApi
    var serializersModule: SerializersModule = EmptySerializersModule

    var writePolymorphic = true
}

@ExperimentalSerializationApi
open class NbtFormat internal constructor(val config: NbtFormatConfig) : SerialFormat {

    override val serializersModule = EmptySerializersModule + config.serializersModule

    companion object Default : NbtFormat(NbtFormatConfig())

    @InternalSerializationApi
    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Any {
        val encoder = TagEncoder(config)
        encoder.encodeSerializableValue(serializer, obj)
        return encoder.root
    }

    @ExperimentalSerializationApi
    @InternalSerializationApi
    inline fun <reified T> encodeToTag(obj: T): Any {
        val encoder = TagEncoder(config)
        encoder.encodeSerializableValue(EmptySerializersModule.serializer(), obj)
        return encoder.root
    }

}

@ExperimentalSerializationApi
fun NbtFormat(config: NbtFormatConfig.() -> Unit): NbtFormat {
    return NbtFormat(NbtFormatConfig().apply(config))
}


