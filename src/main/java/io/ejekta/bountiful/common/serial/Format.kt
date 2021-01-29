package io.ejekta.bountiful.common.serial

import kotlinx.serialization.json.Json

object Format {
    val DataPack = Json {
        encodeDefaults = true
        prettyPrint = true
    }
    val NBT = Json {
        //encodeDefaults = true
    }
}