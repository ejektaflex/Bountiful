package io.ejekta.bountiful.common.bounty.data.pool

import io.ejekta.bountiful.common.content.BountifulContent
import kotlinx.serialization.Serializable

@Serializable
data class Decree(
    var id: String = "DEFAULT_DECREE",
    val objectives: MutableSet<String>,
    val rewards: MutableSet<String>
    ) {

    val objectivePools: List<Pool>
        get() = objectives.map { id ->
            BountifulContent.Pools.first { it.id == id }
        }

    val rewardPools: List<Pool>
        get() = rewards.map { id ->
            BountifulContent.Pools.first { it.id == id }
        }

}