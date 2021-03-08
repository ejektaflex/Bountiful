package io.ejekta.kambrikx.api.serial.nbt

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.api.serial.serializers.StringTagSerializer
import io.ejekta.kambrikx.internal.serial.decoders.TagDecoder
import io.ejekta.kambrikx.internal.serial.decoders.TagMapDecoder
import io.ejekta.kambrikx.internal.serial.decoders.TaglessDecoder
import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TaglessEncoder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import net.minecraft.nbt.*

@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
class NbtFormatConfig {

    private val nbtEncodingMarker = Kambrik.Logging.createMarker("NBT-SERIAL")

    private val logger = Kambrik.Logger

    internal fun logInfo(level: Int, msg: String) {
        logger.info(nbtEncodingMarker, "\t".repeat(level) + msg)
    }

    var classDiscriminator: String = "type"

    var serializersModule: SerializersModule = EmptySerializersModule

    var writePolymorphic = true

    var nullTag: Tag = StringTag.of("_serial_null")
}

@OptIn(InternalSerializationApi::class)
open class NbtFormat internal constructor(val config: NbtFormatConfig) : SerialFormat {

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule = EmptySerializersModule + config.serializersModule

    companion object Default : NbtFormat(NbtFormatConfig()) {
        val BuiltInSerializers = SerializersModule {
            contextual(StringTag::class, StringTagSerializer)
        }
    }

    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Tag {
        return when (serializer.descriptor.kind) {
            is PrimitiveKind -> {
                val enc = TaglessEncoder(config)
                enc.encodeSerializableValue(serializer, obj)
                enc.root
            }
            else -> {
                val enc = TagEncoder(config)
                enc.encodeSerializableValue(serializer, obj)
                enc.root
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> encodeToTag(obj: T) = encodeToTag(EmptySerializersModule.serializer(), obj)

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> decodeFromTag(deserializer: DeserializationStrategy<T>, tag: Tag): T {
        println("Decoding: ${tag::class.simpleName}")
        val decoder = when (tag) {
            is CompoundTag, is ListTag -> TagDecoder(config, 0, tag)
            else -> TaglessDecoder(config, 0, tag)
        }
        return decoder.decodeSerializableValue(deserializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> decodeFromTag(tag: Tag) = decodeFromTag<T>(EmptySerializersModule.serializer(), tag)

}

@OptIn(InternalSerializationApi::class)
fun NbtFormat(config: NbtFormatConfig.() -> Unit): NbtFormat {
    return NbtFormat(NbtFormatConfig().apply(config))
}


