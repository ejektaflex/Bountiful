package io.ejekta.bountiful.config

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.json.Json

object JsonFormats {
    val DataPack = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        encodeDefaults = true
        prettyPrint = true
    }
    val Hand = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
        prettyPrint = true
    }
}