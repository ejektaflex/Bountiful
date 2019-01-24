package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.logic.picked.PickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack

data class PresetBounty(
        override val objectives: MutableList<BountyObjective> = mutableListOf(),
        override val rewards: MutableList<BountyReward> = mutableListOf()
) : IPresetBounty {

    // Serialization friendly content

    inner class BountyObjective(var content: String = "UNDEFINED", var amount: Int) {

    }

    inner class BountyReward(var content: String = "UNDEFINED", var amount: Int) {

    }

    override var weight = 100

    override fun isValid(): Boolean {
        val getsValid = objectiveContent().all { it.contentObj != null }
        val rewardsValid = rewardContent().all { (it.contentObj is PickedEntryStack) && (it.contentObj != null) }
        return getsValid && rewardsValid
    }

    override fun objectiveContent() = objectives.map { PickedEntry(it.content, it.amount).typed() }

    override fun rewardContent() = rewards.map { PickedEntry(it.content, it.amount).typed() }

}
