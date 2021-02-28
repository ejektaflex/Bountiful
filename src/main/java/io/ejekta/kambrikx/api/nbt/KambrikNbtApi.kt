package io.ejekta.kambrikx.api.nbt

import io.ejekta.kambrikx.ext.toJson
import io.ejekta.kambrikx.ext.toLenientJson
import io.ejekta.kambrikx.ext.toLenientTag
import io.ejekta.kambrikx.ext.toTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.minecraft.nbt.Tag

class KambrikNbtApi internal constructor() {
    fun <T> toNbt(serializer: KSerializer<T>, obj: T, format: Json = Json, mode: NbtMode = NbtMode.LENIENT): Tag {
        return format.encodeToJsonElement(serializer, obj).toTag(mode)
    }

    inline fun <reified T> toNbt(obj: T, format: Json = Json, mode: NbtMode = NbtMode.LENIENT): Tag {
        return format.encodeToJsonElement(obj).toTag(mode)
    }

    fun <T> fromNbt(serializer: KSerializer<T>, tag: Tag, format: Json = Json, mode: NbtMode = NbtMode.LENIENT): T {
        return format.decodeFromJsonElement(serializer, tag.toJson(mode))
    }

    inline fun <reified T> fromNbt(tag: Tag, format: Json = Json, mode: NbtMode = NbtMode.LENIENT): T {
        return format.decodeFromJsonElement<T>(tag.toJson(mode))
    }

}