package io.ejekta.kambrikx.serial.convert

import io.ejekta.kambrik.ext.internal.tagType
import io.ejekta.kambrikx.serial.TagType
import io.ejekta.kambrikx.serial.convert.BoxedTag.Companion.TAG_DATA
import io.ejekta.kambrikx.serial.convert.BoxedTag.Companion.TAG_TYPE
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

object TagConverterStrict : TagConverter() {


    override fun toTag(jsonArray: JsonArray): Tag {
        TODO()
    }

    override fun toTag(jsonObject: JsonObject): Tag {
        val failsafe = { TagConverterLenient.toTag(jsonObject) }
        jsonObject.run {
            if (TAG_TYPE in this && TAG_DATA in this) {
                val tt = (get(TAG_TYPE) as? JsonPrimitive) ?: return failsafe()
                val td = get(TAG_DATA) ?: return failsafe()
                if (!tt.isString) {
                    return failsafe()
                }
                val typed = TagType.values().find { it.shortname == tt.content } ?: return failsafe()
                return convertToTag(typed, td)
            } else {
                return failsafe()
            }
        }
    }

    private fun convertToTag(type: TagType, element: JsonElement): Tag {
        return when (type) {
            TagType.INT_ARRAY_TAG -> toIntArrayTag(element.jsonArray)
            TagType.LONG_ARRAY_TAG -> toLongArrayTag(element.jsonArray)
            else -> throw Exception("Could not convert tag of type: $type, we must implement it yet!")
        }
    }

    override fun toJson(tag: Tag): JsonElement {
        return when (tag) {
            is CompoundTag -> fromCompoundTag(tag)
            else -> BoxedTag.of(tag).data
        }
    }

}