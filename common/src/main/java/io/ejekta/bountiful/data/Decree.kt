package io.ejekta.bountiful.data

import io.ejekta.bountiful.content.BountifulContent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@Serializable
data class Decree(
    override var id: String = "DEFAULT_DECREE",
    val objectives: MutableSet<String>,
    val rewards: MutableSet<String>,
    override val requires: MutableList<String> = mutableListOf(),
    override val replace: Boolean = false,
    val name: String? = null,
    val canSpawn: Boolean = true,
    val canReveal: Boolean = true,
    val canWanderBuy: Boolean = true,
    val linkedProfessions: List<String> = emptyList()
    ) : IMerge<Decree> {

    val objectivePools: List<Pool>
        get() = objectives.mapNotNull { id ->
            BountifulContent.Pools.find { it.id == id }
        }

    val rewardPools: List<Pool>
        get() = rewards.mapNotNull { id ->
            BountifulContent.Pools.find { it.id == id }
        }

    val invalidPools: List<String>
        get() = allPoolIds.groupBy { id -> BountifulContent.Pools.find { it.id == id } }[null] ?: emptyList()

    val allPoolIds: Set<String>
        get() = objectives + rewards

    val translation: MutableText
        get() = Text.translatable("bountiful.decree.$id.name")

    val allObjectiveEntries: List<PoolEntry>
        get() = objectivePools.map { it.items }.flatten()

    val allRewardEntries: List<PoolEntry>
        get() = rewardPools.map { it.items }.flatten()

    override fun merged(other: Decree): Decree {
        return Decree(
            id,
            (objectives + other.objectives).toMutableSet(),
            (rewards + other.rewards).toMutableSet(),
            (requires + other.requires).toSet().toMutableList(),
            replace || other.replace,
            name ?: other.name,
            canSpawn || other.canSpawn,
            canReveal || other.canReveal,
            canWanderBuy || other.canWanderBuy,
            linkedProfessions + other.linkedProfessions
        )
    }

}