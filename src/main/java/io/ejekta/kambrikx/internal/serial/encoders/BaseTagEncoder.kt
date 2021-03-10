package io.ejekta.kambrikx.internal.serial.encoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.*

@InternalSerializationApi
abstract class BaseTagEncoder(
    @JvmField protected val config: NbtFormatConfig,
    private var level: Int = 0,
    open val onEnd: (Tag) -> Unit = {},
) : NamedValueEncoder() {

    abstract val root: Tag
    abstract fun addTag(name: String?, tag: Tag)

    open val propogate: Tag.() -> Unit = {
        addTag(currentTagOrNull, this)
    }

    var encodePolymorphic: Boolean = false

    @ExperimentalSerializationApi
    override val serializersModule: SerializersModule = config.serializersModule

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        config.logInfo(level, "Parse: ${descriptor.kind}")
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListEncoder(config, level + 1, propogate)
            StructureKind.CLASS -> TagClassEncoder(config, level + 1, propogate).also {
                if (encodePolymorphic) {
                    encodePolymorphic = false
                    it.addTag(config.classDiscriminator, StringTag.of(descriptor.serialName))
                }
            }
            StructureKind.MAP -> TagMapEncoder(config, level + 1, propogate)
            else -> throw Exception("Could not begin ! Was a: ${descriptor.kind}")
        }
    }

    @ExperimentalSerializationApi
    override fun elementName(descriptor: SerialDescriptor, index: Int): String {
        return if (descriptor.kind is PolymorphicKind) index.toString() else super.elementName(descriptor, index)
    }

    override fun composeName(parentName: String, childName: String) = childName // Leave only base name

    @ExperimentalSerializationApi
    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        onEnd(root)
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    private fun <T: Any> getPolymorphicSerializer(ser: SerializationStrategy<T>, value: T): SerializationStrategy<T> {
        val abs = ser as AbstractPolymorphicSerializer<T>
        return abs.findPolymorphicSerializer(this, value)
    }

    @ExperimentalSerializationApi
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (value is Any) {
            val open = serializer.descriptor.kind is PolymorphicKind.OPEN
            val serial = when (serializer.descriptor.kind) {
                is PolymorphicKind.OPEN -> {
                    encodePolymorphic = true
                    getPolymorphicSerializer(serializer, value)
                }
                else -> serializer
            }
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            config.logInfo(level, "Kind: ${serial.descriptor.kind::class.simpleName} (Value: ${value!!::class.simpleName}, Open: $open)")
            super.encodeSerializableValue(serial, value)
        } else {
            throw SerializationException("Trying to encode $value, which is not a subtype of 'Any'! D:")
        }
    }

    override fun encodeTaggedInt(tag: String, value: Int) { addTag(tag, IntTag.of(value)) }
    override fun encodeTaggedString(tag: String, value: String) { addTag(tag, StringTag.of(value)) }
    override fun encodeTaggedBoolean(tag: String, value: Boolean) { addTag(tag, ByteTag.of(value)) }
    override fun encodeTaggedDouble(tag: String, value: Double) { addTag(tag, DoubleTag.of(value)) }
    override fun encodeTaggedByte(tag: String, value: Byte) { addTag(tag, ByteTag.of(value)) }
    override fun encodeTaggedChar(tag: String, value: Char) { addTag(tag, StringTag.of(value.toString())) }
    override fun encodeTaggedFloat(tag: String, value: Float) { addTag(tag, FloatTag.of(value)) }
    override fun encodeTaggedLong(tag: String, value: Long) { addTag(tag, LongTag.of(value)) }
    override fun encodeTaggedShort(tag: String, value: Short) { addTag(tag, ShortTag.of(value)) }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor, ordinal: Int) {
        val element = enumDescriptor.getElementName(ordinal)
        addTag(tag, StringTag.of(element))
    }

    override fun encodeTaggedNull(tag: String) {
        addTag(tag, config.nullTag)
    }

}