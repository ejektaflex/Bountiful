package io.ejekta.bountiful.util

import io.ejekta.bountiful.mixin.MutableListTag
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

/*
 For auto conversion of Json to NBT and back to Json.

 */
object JsonStrict {

    fun JsonElement.toTag(): Tag {
        return when (this) {
            is JsonObject -> toTag()
            is JsonPrimitive -> toTag()
            is JsonArray -> toTag()
            else -> throw Exception("Nope!")
        }
    }

    fun JsonObject.toTag(): Tag {
        return CompoundTag().apply {
            forEach { k, v ->
                put(k, v.toTag())
            }
        }
    }

    fun JsonPrimitive.toTag(): Tag {
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

    fun JsonArray.toTag(): ListTag {
        val firstItem = firstOrNull() ?: return ListTag()

        val list = ListTag() as MutableListTag
        list.setTagType((-37).toByte())
        list.items.addAll(map { it.toTag() })
        return list as ListTag
    }

    fun Tag.toJson(): JsonElement {
        return when (this) {
            is CompoundTag -> toJson()
            is ListTag -> toJson()
            is StringTag -> JsonPrimitive(asString())
            is IntTag -> JsonPrimitive(int)
            is DoubleTag -> JsonPrimitive(double)
            is LongTag -> JsonPrimitive(long)
            is FloatTag -> JsonPrimitive(float)
            // Weird territory below
            is IntArrayTag -> toJson()
            is ByteArrayTag -> toJson()
            //is LongArrayTag -> toJson()
            // Even weirder territory below
            is ByteTag -> JsonPrimitive(byte)
            is ShortTag -> JsonPrimitive(short)
            else -> throw Exception("Not implemented yet! I'm a: ${this::class.simpleName}")
        }
    }

    fun ListTag.toJson(): JsonArray {
        return JsonArray((this as MutableListTag).items.map { it.toJson() })
    }

    fun CompoundTag.toJson(): JsonObject {
        if (isEmpty) return buildJsonObject {  }
        return JsonObject(keys.map { it to get(it)!!.toJson() }.toMap())
    }


}