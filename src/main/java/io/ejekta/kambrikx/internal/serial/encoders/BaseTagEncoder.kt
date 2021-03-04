package io.ejekta.kambrikx.internal.serial.encoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import net.minecraft.nbt.*
import kotlin.reflect.KClass

@InternalSerializationApi
abstract class BaseTagEncoder(
    @JvmField protected val config: NbtFormatConfig,
    open val onEnd: Tag.() -> Unit = {}
) : NamedValueEncoder() {

    abstract val root: Tag
    abstract fun addTag(name: String?, tag: Tag)

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListEncoder(config) { addTag(currentTagOrNull, it) }
            StructureKind.CLASS -> TagClassEncoder(config) { addTag(currentTagOrNull, it) }
            StructureKind.MAP -> TagMapEncoder(config) { addTag(currentTagOrNull, it) }
            else -> throw Exception("Could not begin ! Was a: ${descriptor.kind}")
        }.apply {
            if (config.writePolymorphic) {
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
    private fun <T: Any> dynamicFindPoly(cap: KClass<*>, value: T): SerializationStrategy<T>? {
        return config.serializersModule.getPolymorphic(cap as KClass<in T>, value)
    }

    @ExperimentalSerializationApi
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (serializer.descriptor.kind is PolymorphicKind.OPEN) {

            val capKlass = serializer.descriptor.capturedKClass

            val polymorphed = dynamicFindPoly(capKlass as KClass<*>, value as Any)
                ?: throw Exception("Could not find a matching polymorphed class for ${serializer.descriptor.serialName} => $value")

            super.encodeSerializableValue(polymorphed as SerializationStrategy<T>, value)
        } else {
            super.encodeSerializableValue(serializer, value)
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