package io.ejekta.bountiful.config

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
object JsonFormats {
    val DataPack = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        prettyPrint = true
        allowTrailingComma = true
    }
    val BlockEntity = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
    }
    val Hand = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        prettyPrint = true
    }
    val Config = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        encodeDefaults = true
        prettyPrint = true
        allowTrailingComma = true
    }
}