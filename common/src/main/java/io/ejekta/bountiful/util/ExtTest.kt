package io.ejekta.bountiful.util

import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.Kambrik
import io.ejekta.percale.reverse.PercaleJson
import kotlinx.serialization.json.Json

fun main() {

    val text = """
        {"type":"item","content":"minecraft:flint","unitWorth":80,"amount":{"min":4,"max":48}}
    """.trimIndent()

    val percaleJson = PercaleJson(JsonOps.INSTANCE, Kambrik.Serial.Format)

    val result = percaleJson.dynamicDecodeFromString(text, PoolEntry.serializer())

    println(result)

}