package io.ejekta.bountiful.config

import kotlinx.serialization.json.Json

object Format {
    val DataPack = Json {
        encodeDefaults = true
        prettyPrint = true
    }
    val Hand = Json {
        prettyPrint = true
    }
    val NBT = Json {
        //encodeDefaults = true
    }
    val Normal = Json {  }
}