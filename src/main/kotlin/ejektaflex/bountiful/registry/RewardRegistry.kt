package ejektaflex.bountiful.registry

import ejektaflex.bountiful.api.logic.picked.PickedEntryStack

object RewardRegistry : ValueRegistry<PickedEntryStack>() {
    fun validRewards(worth: Int, alreadyPicked: List<String>): List<PickedEntryStack> {
        return validRewards().filter { it.amount <= worth && it.content !in alreadyPicked }.sortedBy { it.amount }
    }

    fun validRewards(worth: Int): List<PickedEntryStack> {
        return validRewards().filter { it.amount <= worth }.sortedBy { it.amount }
    }

    fun validRewards(): List<PickedEntryStack> {
        return items
    }
}