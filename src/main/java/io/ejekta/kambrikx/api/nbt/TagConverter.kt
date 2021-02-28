package io.ejekta.kambrikx.api.nbt

import kotlinx.serialization.json.*
import net.minecraft.nbt.*

abstract class TagConverter {


    abstract fun fromJsonObject(jsonObject: JsonObject): Tag

    abstract fun toJson(tag: Tag): JsonElement

    open fun toTag(jsonElement: JsonElement): Tag {
        throw Exception("Nope!")
    }

    protected fun fromJsonPrimitive(jsonPrimitive: JsonPrimitive): Tag {
        jsonPrimitive.run {
            return when {
                isString -> StringTag.of(content)
                booleanOrNull != null -> ByteTag.of(boolean)
                intOrNull != null -> IntTag.of(int)
                doubleOrNull != null -> DoubleTag.of(double)
                longOrNull != null -> LongTag.of(long)
                floatOrNull != null -> FloatTag.of(float)
                else -> throw Exception("Cannot convert this primitive to a tag!")
            }
        }
    }

    protected fun fromCompoundTag(compoundTag: CompoundTag): JsonObject {
        if (compoundTag.isEmpty) return buildJsonObject {  }
        val keyMap = compoundTag.keys.map<String, Pair<String, JsonElement>> {
            it to toJson(compoundTag.get(it)!!)
        }.toMap()
        return JsonObject(keyMap)
    }

    companion object {


        fun fromNumberArrayTag(numArrayTag: AbstractListTag<out AbstractNumberTag>): JsonArray {
            return buildJsonArray {
                for (num in numArrayTag) {
                    add(num.number)
                }
            }
        }

        fun toLongArrayTag(jsonArray: JsonArray): LongArrayTag {
            return LongArrayTag(jsonArray.map { it.jsonPrimitive.long })
        }

        fun toIntArrayTag(jsonArray: JsonArray): IntArrayTag {
            return IntArrayTag(jsonArray.map { it.jsonPrimitive.int })
        }

        fun toByteArrayTag(jsonArray: JsonArray): ByteArrayTag {
            return ByteArrayTag(jsonArray.map { it.jsonPrimitive.content.toByte() })
        }

        fun toStringTag(jsonPrimitive: JsonPrimitive): StringTag {
            return StringTag.of(jsonPrimitive.content)
        }

    }


}