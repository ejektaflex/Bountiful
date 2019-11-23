package ejektaflex.bountiful.api.data.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlin.reflect.KClass

object JsonAdapter {

    var deserializers = mutableMapOf<Class<*>, JsonDeserializer<*>>()

    inline fun <reified T : Any> register(deserializer: JsonDeserializer<T>) {
        deserializers[T::class.java] = deserializer
    }

    val gson: Gson
        get() = buildGson()

    fun buildGson(): Gson {
        var gsonProto = GsonBuilder().setPrettyPrinting()
        for (deser in deserializers) {
            gsonProto = gsonProto.registerTypeAdapter(deser.key, deser.value)
        }
        return gsonProto.create()
    }


    inline fun <reified T : Any> fromJson(json: JsonElement): T {
        return gson.fromJson<T>(json, T::class.java)
    }

    fun <T : Any> fromJson(json: JsonElement, klazz: KClass<T>): T {
        return gson.fromJson(json, klazz.java)
    }

    inline fun <reified T : Any> fromJson(json: String): T {
        return gson.fromJson<T>(json, T::class.java)
    }

    inline fun <reified T : Any> toJson(obj: T): String {
        return gson.toJson(obj, T::class.java)
    }

    fun toJson(obj: Any, klazz: KClass<*>): String {
        return gson.toJson(obj, klazz.java)
    }

}