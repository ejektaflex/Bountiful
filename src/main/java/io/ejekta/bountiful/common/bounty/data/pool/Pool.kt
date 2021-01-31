package io.ejekta.bountiful.common.bounty.data.pool

import kotlinx.serialization.Serializable

@Serializable
data class Pool(
    val id: String = "DEFAULT_POOL",
    val content: MutableList<PoolEntry> = mutableListOf()
) {

    fun pick() {



    }

}