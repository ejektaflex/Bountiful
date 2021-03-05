package io.ejekta.kambrikx.internal.serial.encoders

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
    open val onEnd: Tag.() -> Unit = {}
) : NamedValueEncoder() {

    abstract val root: Tag
    abstract fun addTag(name: String?, tag: Tag)

    var encodePolymorphic: Boolean = config.writePolymorphic

    @ExperimentalSerializationApi
    override val serializersModule: SerializersModule = config.serializersModule

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        //println("Kind: ${descriptor.kind}")
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListEncoder(config) { addTag(currentTagOrNull, it) }
            StructureKind.CLASS -> TagClassEncoder(config) { addTag(currentTagOrNull, it) }
            StructureKind.MAP -> TagMapEncoder(config) { addTag(currentTagOrNull, it) }
            else -> throw Exception("Could not begin ! Was a: ${descriptor.kind}")
        }.apply {
            if (encodePolymorphic) {
                encodePolymorphic = false
                addTag(config.classDiscriminator, StringTag.of(descriptor.serialName))
            }
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
        root.onEnd()
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    private fun <T: Any> getPolymorphicSerializer(ser: SerializationStrategy<T>, value: T): SerializationStrategy<T> {
        val abs = ser as AbstractPolymorphicSerializer<T>
        return abs.findPolymorphicSerializer(this, value)
    }

    @ExperimentalSerializationApi
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        //println("Desc kind: ${serializer.descriptor.kind}")
        if (value is Any) {
            if (serializer.descriptor.kind is PolymorphicKind.OPEN) {
                val polymorphicSerializer = getPolymorphicSerializer(serializer, value)
                super.encodeSerializableValue(polymorphicSerializer, value)
            } else {
                //println("Ser: $serializer, Val: $value, Kind: ${serializer.descriptor.kind::class.qualifiedName}")
                super.encodeSerializableValue(serializer, value)
            }
        } else {
            throw Exception("Trying to encode $value, which is not a subtype of 'Any'! D:")
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

}