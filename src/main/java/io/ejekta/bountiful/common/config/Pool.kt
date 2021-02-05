package io.ejekta.bountiful.common.config

import kotlinx.serialization.Serializable

@Serializable
data class Pool(
    var id: String = "DEFAULT_POOL",
    val content: MutableList<PoolEntry> = mutableListOf(),
    val replace: Boolean = false
) : IMerge<Pool> {

    override fun merge(other: Pool) {
        when (other.replace) {
            true -> {
                content.clear()
                content.addAll(other.content)
            }
            false -> content.addAll(other.content)
        }
    }

    override fun merged(other: Pool): Pool {
        return when (other.replace) {
            true -> Pool(id, other.content)
            else -> Pool(id, (content + other.content).toMutableList())
        }
    }

}