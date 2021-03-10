package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.*

@InternalSerializationApi
abstract class BaseTagDecoder(
    @JvmField protected val config: NbtFormatConfig,
    var level: Int = 0
) : NamedValueDecoder() {

    abstract val root: Tag

    private fun currentTag(): Tag = currentTagOrNull?.let { readTag(it) } ?: root

    open fun readTag(name: String): Tag {
        return root
    }

    @ExperimentalSerializationApi
    override val serializersModule: SerializersModule = config.serializersModule

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        config.logInfo(level, "Parse: ${descriptor.kind} with tag ${currentTag()} (am: ${this::class.simpleName})")
        return when (descriptor.kind) {
            is PolymorphicKind -> TagMapDecoder(config, level + 1, currentTag())
            StructureKind.CLASS -> TagClassDecoder(config, level + 1, currentTag())
            StructureKind.LIST -> TagListDecoder(config, level + 1, currentTag())
            StructureKind.MAP -> TagMapDecoder(config, level + 1, currentTag())
            else -> throw Exception("Cannot decode a ${descriptor.kind} yet with beginStructure!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    private fun <T : Any> getPolymorphicDeserializer(ser: DeserializationStrategy<T>): DeserializationStrategy<out T> {
        val abs = ser as AbstractPolymorphicSerializer<T>
        val typed = (currentTag() as CompoundTag).getString(config.classDiscriminator)
        if (typed == "") {
            throw SerializationException("Tried to get the polymorphic serializer in ${currentTag()} but failed!")
        }
        return abs.findPolymorphicSerializer(this, typed)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T : Any> getContextualDeserialized(ser: ContextualSerializer<T>): T {
        return ser.deserialize(this)
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {

        if (deserializer !is AbstractPolymorphicSerializer<*>) {
            return deserializer.deserialize(this)
        }

        val actualSerializer = getPolymorphicDeserializer(deserializer as DeserializationStrategy<Any>) as DeserializationStrategy<T>

        return TagMapDecoder(config, level, currentTag()).decodeSerializableValue(actualSerializer)
    }

    override fun decodeTaggedInt(tag: String): Int = (readTag(tag) as IntTag).int
    override fun decodeTaggedString(tag: String): String = (readTag(tag) as StringTag).asString()
    override fun decodeTaggedBoolean(tag: String): Boolean = (readTag(tag) as ByteTag).byte > 0
    override fun decodeTaggedDouble(tag: String): Double = (readTag(tag) as DoubleTag).double
    override fun decodeTaggedByte(tag: String): Byte = (readTag(tag) as ByteTag).byte
    override fun decodeTaggedChar(tag: String): Char = (readTag(tag) as StringTag).asString().first()
    override fun decodeTaggedFloat(tag: String): Float = (readTag(tag) as FloatTag).float
    override fun decodeTaggedLong(tag: String): Long = (readTag(tag) as LongTag).long
    override fun decodeTaggedShort(tag: String): Short = (readTag(tag) as ShortTag).short

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex((readTag(tag) as StringTag).asString())
    }

    override fun decodeTaggedNull(tag: String): Nothing? = null

    override fun decodeTaggedNotNullMark(tag: String): Boolean {
        val read = readTag(tag)
        println("Reading?: ${read::class.simpleName}")
        return read != config.nullTag
    }
}