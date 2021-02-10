package io.ejekta.bountiful.common.config

import kotlinx.serialization.Serializable

@Serializable
data class Pool(
    override var id: String = "DEFAULT_POOL",
    val content: MutableList<PoolEntry> = mutableListOf(),
    val replace: Boolean = false,
    override val requires: MutableList<String> = mutableListOf()
) : IMerge<Pool> {

    fun setup(newId: String) {
        id = newId
        // Do weight normalization
        val overallMult = content.size
        content.takeIf { it.isNotEmpty() }?.forEach {
            it.weightMult /= overallMult
        }
    }

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