package io.ejekta.bountiful.config

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import io.ejekta.percale.contextualCodec
import io.ejekta.percale.reverse.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.minecraft.nbt.StringTag
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
    val Hand = Json {
        serializersModule = MojangSerializer
        prettyPrint = true
    }
    val Config = PercaleJson(JsonOps.INSTANCE, Json {
        serializersModule = MojangSerializer
        encodeDefaults = true
        prettyPrint = true
        allowTrailingComma = true
    })
}