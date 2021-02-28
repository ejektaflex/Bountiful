package io.ejekta.kambrikx.serial.convert

import io.ejekta.kambrikx.serial.JsonStrict.toLenientTag
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

object TagConverterLenient : TagConverter() {

    override fun toTag(jsonElement: JsonElement): Tag {
        return when (jsonElement) {
            is JsonObject -> fromJsonObject(jsonElement)
            is JsonPrimitive -> fromJsonPrimitive(jsonElement)
            is JsonArray -> fromJsonArray(jsonElement)
            else -> super.toTag(jsonElement)
        }
    }

    private fun fromJsonArray(jsonArray: JsonArray): Tag {
        /*
        jsonArray.run {
            val list = ListTag() as MutableListTag
            list.setTagType((-37).toByte())
            list.items.addAll(map { toTag(it) })
            return list as ListTag
        }
         */
        return ListTag().apply {
            addAll(
                jsonArray.map { toTag(it) }
            )
        }
    }

    private fun toJsonArray(tag: AbstractListTag<*>): JsonArray {
        return buildJsonArray {
            for (item in tag) {
                add(toJson(item))
            }
        }
    }

    override fun fromJsonObject(jsonObject: JsonObject): Tag {
        return CompoundTag().apply {
            jsonObject.forEach { k, v ->
                put(k, v.toLenientTag())
            }
        }
    }

    override fun toJson(tag: Tag): JsonElement {
        tag.run {
            @Suppress("UNCHECKED_CAST")
            return when (this) {
                is CompoundTag -> fromCompoundTag(this)
                is AbstractNumberTag -> JsonPrimitive(number)
                is ListTag -> toJsonArray(this)
                is StringTag -> JsonPrimitive(asString())
                is LongArrayTag, is IntArrayTag, is ByteArrayTag -> fromNumberArrayTag(tag as AbstractListTag<out AbstractNumberTag>)
                else -> throw Exception("Not implemented yet! I'm a: ${this::class.simpleName}")
            }
        }
    }

}