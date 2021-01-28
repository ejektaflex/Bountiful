package io.ejekta.bountiful.common.bounty.pool

import kotlinx.serialization.Serializable

@Serializable
abstract class PoolEntry {
    var range = 0 to 0
    var content = "NO_CONTENT"

}