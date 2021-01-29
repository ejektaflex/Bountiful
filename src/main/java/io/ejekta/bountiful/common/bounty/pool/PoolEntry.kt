package io.ejekta.bountiful.common.bounty.pool

import io.ejekta.bountiful.common.bounty.BountyType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JsonObject

@Serializable
abstract class PoolEntry {
    var type = BountyType.ITEM
    var range = EntryRange(1, 1)
    var content = "NO_CONTENT"
    var nbt: JsonObject? = null

    @Serializable
    class EntryRange(val min: Int, val max: Int)
}