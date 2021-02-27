package io.ejekta.kambrikx.serial.convert

import io.ejekta.bountiful.mixin.MutableListTag
import io.ejekta.kambrikx.serial.JsonStrict.toLenientTag
import kotlinx.serialization.json.*
import net.minecraft.nbt.*

object TagConverterLenient : TagConverter() {

    override fun toTag(jsonArray: JsonArray): Tag {
        jsonArray.run {
            val list = ListTag() as MutableListTag
            list.setTagType((-37).toByte())
            list.items.addAll(map { it.toLenientTag() })
            return list as ListTag
        }
    }

    override fun toTag(jsonObject: JsonObject): Tag {
        return CompoundTag().apply {
            jsonObject.forEach { k, v ->
                put(k, v.toLenientTag())
            }
        }
    }

    override fun toJson(tag: Tag): JsonElement {
        tag.run {
            return when (this) {
                is CompoundTag -> fromCompoundTag(this)
                is ListTag -> toJson(this)
                is StringTag -> JsonPrimitive(asString())
                is IntTag -> JsonPrimitive(int)
                is DoubleTag -> JsonPrimitive(double)
                is LongTag -> JsonPrimitive(long)
                is FloatTag -> JsonPrimitive(float)
                // Weird territory below
                //is IntArrayTag -> toJson(this)
                //is ByteArrayTag -> toJson(this)
                is LongArrayTag -> fromNumberArrayTag(this)
                // Even weirder territory below
                //is ByteTag -> JsonPrimitive(byte)
                //is ShortTag -> JsonPrimitive(short)
                else -> throw Exception("Not implemented yet! I'm a: ${this::class.simpleName}")
            }
        }
    }

}