package io.ejekta.bountiful.config

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object JsonFormats {
    val DataPack = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        encodeDefaults = true
        prettyPrint = true
    }
    val BlockEntity = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
    }
    val Hand = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        prettyPrint = true
    }
    @OptIn(ExperimentalSerializationApi::class)
    val Config = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        prettyPrint = true
        allowTrailingComma = true // remove inexperienced JSON user footguns
    }
}