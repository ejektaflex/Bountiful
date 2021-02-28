package io.ejekta.kambrikx.api.nbt

import io.ejekta.kambrikx.api.nbt.BoxedTag.Companion.TAG_DATA
import io.ejekta.kambrikx.api.nbt.BoxedTag.Companion.TAG_TYPE
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

object TagConverterStrict : TagConverter() {

    override fun toJson(tag: Tag): JsonElement {
        return when (tag) {
            is CompoundTag -> fromCompoundTag(tag)
            else -> toBoxedJson(tag)
        }
    }

    override fun toTag(jsonElement: JsonElement): Tag {
        return when (jsonElement) {
            is JsonObject -> fromJsonObject(jsonElement)
            is JsonPrimitive -> fromJsonPrimitive(jsonElement)
            else -> super.toTag(jsonElement)
        }
    }

    override fun fromJsonObject(jsonObject: JsonObject): Tag {
        val failsafe = { toCompoundTag(jsonObject) }
        jsonObject.run {
            if (TAG_TYPE in this && TAG_DATA in this) {
                val tt = (get(TAG_TYPE) as? JsonPrimitive) ?: return failsafe()
                val td = get(TAG_DATA) ?: return failsafe()
                if (!tt.isString) {
                    return failsafe()
                }
                return Json.decodeFromJsonElement(BoxedTag.serializer(), this).toTag()
            } else {
                return failsafe()
            }
        }
    }

    private fun toCompoundTag(jsonObject: JsonObject): CompoundTag {
        return CompoundTag().apply {
            jsonObject.forEach { k, v ->
                put(k, toTag(v))
            }
        }
    }

    private fun toBoxedJson(tag: Tag): JsonObject {
        return Json.encodeToJsonElement(BoxedTag.serializer(), BoxedTag.of(tag)).jsonObject
    }

    private fun fromBoxedJson(boxedTag: BoxedTag): Tag {
        return boxedTag.toTag()
    }


}