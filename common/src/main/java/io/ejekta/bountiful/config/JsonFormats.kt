package io.ejekta.bountiful.config

import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import io.ejekta.percale.reverse.PercaleJson
import io.ejekta.percale.reverse.toSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.util.ExtraCodecs

typealias GsonElement = com.google.gson.JsonElement
typealias GsonObject = com.google.gson.JsonObject

@OptIn(ExperimentalSerializationApi::class)
object JsonFormats {

    val MojangSerializer = SerializersModule {
        include(Kambrik.Serial.DefaultSerializers)

//        contextual(GsonElementSerializer)
//        contextual(GsonObjectSerializer)
//        contextual(GsonStringSerializer)

        contextual(GsonElement::class, ExtraCodecs.JSON.toSerializer(JsonElement.serializer()))

        contextual(GsonObject::class, ExtraCodecs.JSON.toSerializer(JsonElement.serializer()) as KSerializer<GsonObject>)

        //contextual(GsonObject::class, ExtraCodecs.JSON.toSerializer(JsonObject.serializer()))

        //contextualCodec(ResourceLocation.CODEC)
        //contextualCodec(NbtCompound.CODEC)
        //contextualCodec(Vec3d.CODEC)
        //contextualCodec(BlockPos.CODEC)
    }

    val DataPack = Json {
        serializersModule = MojangSerializer
        prettyPrint = true
        allowTrailingComma = true
    }
    val BlockEntity = Json {
        serializersModule = MojangSerializer
    }
    val Hand = PercaleJson(JsonOps.INSTANCE, Json {
        serializersModule = MojangSerializer
        encodeDefaults = false
        prettyPrint = true
    })
    val Config = PercaleJson(JsonOps.INSTANCE, Json {
        serializersModule = MojangSerializer
        encodeDefaults = true
        prettyPrint = true
        allowTrailingComma = true
    })
}