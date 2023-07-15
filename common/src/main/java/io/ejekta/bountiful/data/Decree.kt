package io.ejekta.bountiful.data

import io.ejekta.bountiful.content.BountifulContent
import kotlinx.serialization.Serializable
import net.minecraft.text.MutableText
import net.minecraft.text.Text

@Serializable
data class Decree(
    override var id: String = "DEFAULT_DECREE",
    val objectives: MutableSet<String>,
    val rewards: MutableSet<String>,
    override val requires: MutableList<String> = mutableListOf(),
    override val replace: Boolean = false,
    val name: String? = null
    ) : IMerge<Decree> {

    val objectivePools: List<Pool>
        get() = objectives.mapNotNull { id ->
            BountifulContent.Pools.find { it.id == id }
        }

    val allPoolIds: Set<String>
        get() = objectives + rewards

    val translation: MutableText
        get() = Text.translatable("bountiful.decree.$id.name")

    val rewardPools: List<Pool>
        get() = rewards.mapNotNull { id ->
            BountifulContent.Pools.find { it.id == id }
        }

    override fun merged(other: Decree): Decree {
        return Decree(
            id,
            (objectives + other.objectives).toMutableSet(),
            (rewards + other.rewards).toMutableSet(),
            (requires + other.requires).toSet().toMutableList(),
            replace || other.replace,
            name ?: other.name
        )
    }

}