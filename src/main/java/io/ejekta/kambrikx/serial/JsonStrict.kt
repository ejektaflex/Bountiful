package io.ejekta.kambrikx.serial

import io.ejekta.kambrikx.serial.convert.TagConverterLenient
import io.ejekta.kambrikx.serial.convert.TagConverterStrict
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

/*
 For auto conversion of Json to NBT and back to Json.

 */
object JsonStrict {

    private const val TAG_TYPE = "_tagtype"
    private const val TAG_DATA = "_tagdata"

    fun Tag.toLenientJson() = TagConverterLenient.toJson(this)
    fun Tag.toStrictJson() = TagConverterStrict.toJson(this)

    fun JsonElement.toStrictTag() = TagConverterStrict.toTag(this)
    fun JsonElement.toLenientTag() = TagConverterLenient.toTag(this)

    /*
    private fun JsonArray.toTag(): ListTag {
        val firstItem = firstOrNull() ?: return ListTag()

        val list = ListTag() as MutableListTag
        list.setTagType((-37).toByte())
        list.items.addAll(map { it.toTag() })
        return list as ListTag
    }

    private fun ListTag.toJson(): JsonArray {
        return JsonArray((this as MutableListTag).items.map { it.toJson() })
    }

    private fun CompoundTag.toJson(): JsonObject {
        if (isEmpty) return buildJsonObject {  }
        return JsonObject(keys.map { it to get(it)!!.toJson() }.toMap())
    }

     */

    // LongArray

    fun LongArrayTag.toJsonX(): JsonObject {
        return buildJsonObject {
            put(TAG_TYPE, TagType.LONG_ARRAY_TAG.shortname)
            putJsonArray(TAG_DATA) {
                for (num in this@toJsonX) {
                    add(num.long)
                }
            }
        }
    }




    private fun JsonArray.toLongArrayTag(): LongArrayTag {
        return LongArrayTag(map { it.jsonPrimitive.long })
    }

}