package io.ejekta.bountiful.config

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.json.Json

object JsonFormats {
    val DataPack = Json {
        serializersModule = NbtFormat.BuiltInSerializers
        encodeDefaults = true
        prettyPrint = true
    }
    val Hand = Json {
        serializersModule = NbtFormat.BuiltInSerializers
        prettyPrint = true
    }
}