package io.ejekta.bountiful.data

import io.ejekta.bountiful.content.BountifulContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Pool(
    @Transient override var id: String = "DEFAULT_POOL",
    val weightMult: Double = 1.0,
    override val replace: Boolean = false,
    override val requires: MutableList<String> = mutableListOf(),
    val content: MutableList<PoolEntry> = mutableListOf(),
) : IMerge<Pool> {

    fun setup(newId: String) {
        id = newId
        // Do weight normalization
        val overallMult = content.size
        content.takeIf { it.isNotEmpty() }?.forEach {
            it.weightMult /= overallMult
            it.weightMult *= this.weightMult
            it.sources.add(id)
        }
    }

    operator fun iterator() = content.iterator()

    val usedInDecrees: List<Decree>
        get() = BountifulContent.Decrees.filter { this.id in it.allPoolIds }

    override fun merged(other: Pool): Pool {
        return other.copy(id = id, content = (this.content + other.content).toMutableList())
    }

}