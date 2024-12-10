package io.ejekta.bountiful.config

import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import io.ejekta.percale.contextualCodec
import io.ejekta.percale.reverse.PercaleJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.NbtCompound
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

@OptIn(ExperimentalSerializationApi::class)
object JsonFormats {

    val MojangSerializer = SerializersModule {
        include(Kambrik.Serial.DefaultSerializers)
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