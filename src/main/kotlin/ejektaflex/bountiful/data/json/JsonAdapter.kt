package ejektaflex.bountiful.data.json

import com.google.gson.*
import kotlin.reflect.KClass

object JsonAdapter {

    var deserializers = mutableMapOf<Class<*>, JsonDeserializer<*>>()
    var serializers = mutableMapOf<Class<*>, JsonSerializer<*>>()

    inline fun <reified T : Any> register(deserializer: JsonDeserializer<T>) {
        deserializers[T::class.java] = deserializer
    }

    inline fun <reified T : Any> register(serializer: JsonSerializer<T>) {
        serializers[T::class.java] = serializer
    }

    val gson: Gson
        get() = buildGson()

    fun buildGson(): Gson {
        //excludeFieldsWithoutExposeAnnotation?
        var gsonProto = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation()
        for (deser in deserializers) {
            gsonProto = gsonProto.registerTypeAdapter(deser.key, deser.value)
        }
        for (ser in serializers) {
            gsonProto = gsonProto.registerTypeAdapter(ser.key, ser.value)
        }
        return gsonProto.create()
    }


    inline fun <reified T : Any> fromJson(json: JsonElement): T {
        return gson.fromJson<T>(json, T::class.java)
    }

    fun <T : Any> fromJson(json: JsonElement, klazz: KClass<T>): T {
        return gson.fromJson(json, klazz.java)
    }

    fun <T : Any> fromJsonExp(json: String, klazz: KClass<T>): T {
        return gson.fromJson(json, klazz.java) as T
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

    fun toJsonTree(obj: Any, klazz: KClass<*>): JsonElement {
        return gson.toJsonTree(obj, klazz.java)
    }

}