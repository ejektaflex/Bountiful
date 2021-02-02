package io.ejekta.bountiful.common.bounty.data.pool

import kotlinx.serialization.Serializable

@Serializable
data class Pool(
    var id: String = "DEFAULT_POOL",
    val content: MutableList<PoolEntry> = mutableListOf(),
    val replace: Boolean = false
) : IMerge<Pool> {

    override fun merged(other: Pool): Pool {
        return when (other.replace) {
            true -> Pool(id, other.content)
            else -> Pool(id, (content + other.content).toMutableList())
        }
    }

}