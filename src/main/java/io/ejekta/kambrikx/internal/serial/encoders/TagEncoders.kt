package io.ejekta.kambrikx.internal.serial.encoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.minecraft.nbt.*

@InternalSerializationApi
class TagEncoder(config: NbtFormatConfig) : BaseTagEncoder(config) {
    override var root: Tag = EndTag.INSTANCE

    // Currently, primitive encodings directly call addTag("PRIMITIVE", tag)
    override fun addTag(name: String?, tag: Tag) {
        root = tag
    }

    override val propogate: Tag.() -> Unit = { root = this }
}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagClassEncoder(config: NbtFormatConfig, level: Int, onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(config, level, onEnd) {
    override var root = CompoundTag()
    override fun addTag(name: String?, tag: Tag) {
        root.put(name, tag)
    }
}

@InternalSerializationApi
class TagListEncoder(config: NbtFormatConfig, level: Int, onEnd: (Tag) -> Unit) : BaseTagEncoder(config, level, onEnd) {
    override val root = ListTag()
    override fun addTag(name: String?, tag: Tag) {
        if (name != config.classDiscriminator) {
            root.add(name!!.toInt(), tag)
        }
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
class TagMapEncoder(config: NbtFormatConfig, level: Int, onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(config, level, onEnd) {
    override var root = CompoundTag()
    private var theKey = ""
    private var isKey = true

    override fun addTag(name: String?, tag: Tag) {
        //println("Boop $name '$theKey' $tag (${tag::class})")
        if (name != config.classDiscriminator) {
            if (isKey) {
                theKey = tag.asString() ?: "DEFAULT_KEY"
            } else {
                root.put(theKey, tag)
            }
            isKey = !isKey
        }
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TaglessEncoder(config: NbtFormatConfig) : AbstractEncoder() {
    override val serializersModule = config.serializersModule
    lateinit var root: Tag
    override fun encodeInt(value: Int) { root = IntTag.of(value) }
    override fun encodeString(value: String) { root = StringTag.of(value) }
    override fun encodeBoolean(value: Boolean) { root = ByteTag.of(value) }
    override fun encodeDouble(value: Double) { root = DoubleTag.of(value) }
    override fun encodeByte(value: Byte) { root = ByteTag.of(value) }
    override fun encodeChar(value: Char) { root = StringTag.of(value.toString()) }
    override fun encodeFloat(value: Float) { root = FloatTag.of(value) }
    override fun encodeLong(value: Long) { root = LongTag.of(value) }
    override fun encodeShort(value: Short) { root = ShortTag.of(value) }
}