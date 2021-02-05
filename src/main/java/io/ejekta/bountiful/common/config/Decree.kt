package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.content.BountifulContent
import kotlinx.serialization.Serializable

@Serializable
data class Decree(
    var id: String = "DEFAULT_DECREE",
    val objectives: MutableSet<String>,
    val rewards: MutableSet<String>
    ) : IMerge<Decree> {

    val objectivePools: List<Pool>
        get() = objectives.map { id ->
            BountifulContent.Pools.first { it.id == id }
        }

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