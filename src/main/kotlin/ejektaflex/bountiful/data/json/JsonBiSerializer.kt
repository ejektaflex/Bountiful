package ejektaflex.bountiful.data.json

import com.google.gson.*
import java.lang.reflect.Type

interface JsonBiSerializer<T> : JsonDeserializer<T>, JsonSerializer<T> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {
        return JsonAdapter.gson.fromJson<T>(json, typeOfT)
    }

    override fun serialize(src: T, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonAdapter.gson.toJsonTree(src, typeOfSrc)
    }

}