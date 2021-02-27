package io.ejekta.kambrikx.serial.convert

import io.ejekta.kambrik.ext.internal.tagType
import io.ejekta.kambrikx.serial.TagType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

@Serializable
data class BoxedTag(
        @SerialName(TAG_TYPE)
        val type: TagType,
        @SerialName(TAG_DATA)
        val data: JsonElement
) {

    fun toTag(): Tag {
        return when (type) {
            // EndTag
            TagType.END_TAG, TagType.COMPOUND_TAG, TagType.LIST_TAG -> throw Exception("Cannot convert a tag with type $type with a BoxedTag!")
            // AbstractNumberTags
            TagType.INT_TAG -> IntTag.of(data.jsonPrimitive.int)
            TagType.DOUBLE_TAG -> DoubleTag.of(data.jsonPrimitive.double)
            TagType.FLOAT_TAG -> FloatTag.of(data.jsonPrimitive.float)
            TagType.SHORT_TAG -> ShortTag.of(data.jsonPrimitive.content.toShort())
            TagType.LONG_TAG -> LongTag.of(data.jsonPrimitive.long)
            TagType.BYTE_TAG -> ByteTag.of(data.jsonPrimitive.content.toByte())
            // AbstractTagList<out AbstractNumberTag>s
            TagType.LONG_ARRAY_TAG -> TagConverter.toLongArrayTag(data.jsonArray)
            TagType.INT_ARRAY_TAG -> TagConverter.toIntArrayTag(data.jsonArray)
            TagType.BYTE_ARRAY_TAG -> TagConverter.toByteArrayTag(data.jsonArray)
            // Others
            TagType.STRING_TAG -> TagConverter.toStringTag(data.jsonPrimitive)
        }
    }

    companion object {

        const val TAG_TYPE = "_tagtype"
        const val TAG_DATA = "_tagdata"

        fun of(tag: Tag): BoxedTag {
            val type = tag.tagType

            @Suppress("UNCHECKED_CAST")
            return when (tag) {
                //is CompoundTag -> BoxedTag(type, TagConverterStrict.toJson(tag))
                is StringTag -> BoxedTag(type, JsonPrimitive(tag.asString()))
                is AbstractNumberTag -> BoxedTag(type, JsonPrimitive(tag.number)) // Dbl/Float/Int/Long/Short Tags
                is LongArrayTag, is IntArrayTag, is ByteArrayTag -> BoxedTag(type, TagConverter.fromNumberArrayTag(tag as AbstractListTag<out AbstractNumberTag>))
                else -> throw Exception("Cannot box tag typed: $type")
            }

        }
    }

}