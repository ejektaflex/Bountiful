package io.ejekta.kambrikx.api.nbt

import io.ejekta.kambrikx.api.nbt.BoxedTag.Companion.TAG_DATA
import io.ejekta.kambrikx.api.nbt.BoxedTag.Companion.TAG_TYPE
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

object TagConverterStrict : TagConverter() {

    override fun toJson(tag: Tag): JsonElement {
        //println("\t\t - Converting tag to JSON: $tag")
        return when (tag) {
            is CompoundTag -> fromCompoundTag(tag)
            else -> toBoxedJson(tag)
        }
    }

    override fun toTag(jsonElement: JsonElement): Tag {
        //println("\t\t - Converting JSON to tag: $jsonElement")
        return when (jsonElement) {
            is JsonObject -> fromJsonObject(jsonElement)
            is JsonPrimitive -> fromJsonPrimitive(jsonElement)
            is JsonArray -> TagConverterLenient.toTag(jsonElement) // Strict serializers should not normally get an array, so we'll do it leniently
            else -> super.toTag(jsonElement)
        }
    }

    private fun toBoxedJson(tag: Tag): JsonObject {
        return Json.encodeToJsonElement(BoxedTag.serializer(), BoxedTag.of(tag)).jsonObject
    }

    private fun fromBoxedJson(boxedTag: BoxedTag): Tag {
        return boxedTag.toTag()
    }


}