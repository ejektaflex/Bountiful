package io.ejekta.bountiful.data

import io.ejekta.bountiful.content.BountifulContent
import kotlinx.serialization.Serializable
import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableText

@Serializable
data class Decree(
    override var id: String = "DEFAULT_DECREE",
    val objectives: MutableSet<String>,
    val rewards: MutableSet<String>,
    override val requires: MutableList<String> = mutableListOf()
    ) : IMerge<Decree> {

    val objectivePools: List<Pool>
        get() = objectives.map { id ->
            BountifulContent.Pools.first { it.id == id }
        }

    val allPoolIds: Set<String>
        get() = objectives + rewards

    val translation: MutableText
        get() = TranslatableText("bountiful.decree.$id.name")

    val rewardPools: List<Pool>
        get() = rewards.map { id ->
            BountifulContent.Pools.first { it.id == id }
        }

    override fun merge(other: Decree) {
        objectives.addAll(other.objectives)
        rewards.addAll(other.rewards)
    }

    override fun merged(other: Decree): Decree {
        return Decree(
            id,
            (objectives + other.objectives).toMutableSet(),
            (rewards + other.rewards).toMutableSet()
        )
    }

}