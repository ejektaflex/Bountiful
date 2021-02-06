package io.ejekta.bountiful.common.config

import kotlinx.serialization.Serializable
import net.fabricmc.loader.api.FabricLoader

@Serializable
data class Pool(
    var id: String = "DEFAULT_POOL",
    val content: MutableList<PoolEntry> = mutableListOf(),
    val replace: Boolean = false,
    val requires: MutableList<String> = mutableListOf()
) : IMerge<Pool> {

    val canLoad: Boolean
        get() = requires.all { FabricLoader.getInstance().isModLoaded(it) }

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