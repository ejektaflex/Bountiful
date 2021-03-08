package io.ejekta.bountiful.config

import kotlinx.serialization.json.Json

object JsonFormats {
    val DataPack = Json {
        encodeDefaults = true
        prettyPrint = true
    }
    val Hand = Json {
        prettyPrint = true
    }
}