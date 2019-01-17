package ejektaflex.bountiful.data

import ejektaflex.bountiful.api.data.IPresetBounty
import ejektaflex.bountiful.api.logic.pickable.PickedEntry
import ejektaflex.bountiful.api.logic.pickable.PickedEntryStack

data class PresetBounty(
        override val objectives: MutableMap<String, Int> = mutableMapOf(),
        override val rewards: MutableMap<String, Int> = mutableMapOf()
) : IPresetBounty {

    override var weight = 100

    override fun isValid(): Boolean {
        val getsValid = objectiveContent().all { it.contentObj != null }
        val rewardsValid = rewardContent().all { (it.contentObj is PickedEntryStack) && (it.contentObj != null) }
        return getsValid && rewardsValid
    }

    override fun objectiveContent() = objectives.map { PickedEntry(it.key, it.value).typed() }

    override fun rewardContent() = rewards.map { PickedEntry(it.key, it.value).typed() }

}
