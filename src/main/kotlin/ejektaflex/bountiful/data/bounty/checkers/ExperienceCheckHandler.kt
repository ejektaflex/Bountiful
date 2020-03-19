package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryEntity
import ejektaflex.bountiful.data.bounty.BountyEntryExperience
import ejektaflex.bountiful.logic.BountyProgress
import kotlin.math.max

class ExperienceCheckHandler : CheckHandler<BountyEntryEntity>() {

    companion object {
        fun incXpForNextLevel(level: Int): Int {
            return when (level) {
                in -1..-1 -> 0
                in 0..15 -> 2 * level + 7
                in 16..30 -> 5 * level - 38
                else -> 9 * level - 158
            }
        }

        fun xpForThisLevel(level: Int) = incXpForNextLevel(level - 1)

        fun xpAtLevel(level: Int): Int {
            return when {
                level <= 0 -> 0
                else -> xpForThisLevel(level) + xpAtLevel(level - 1)
            }
        }
    }

    override fun fulfill() {
        val xpObjs = data.objectives.content.filterIsInstance<BountyEntryExperience>()

        var levelsToRemove = 0
        var pointsToRemove = 0

        for (obj in xpObjs) {
            when (obj.content) {
                "levels" -> levelsToRemove += obj.amount
                "points" -> pointsToRemove += obj.amount
                else -> BountifulMod.logger.error("Trying to remove ${obj.content} from player by fulfilling bounty, wat?")
            }
        }

        val xpNeededForLevels = player.experienceTotal - xpAtLevel(player.experienceLevel - levelsToRemove)

        var totalXpToRemove = max(xpNeededForLevels, pointsToRemove)

        player.giveExperiencePoints(-totalXpToRemove)

    }

    override fun objectiveStatus(): Map<BountyEntry, BountyProgress> {

        var succ = mutableMapOf<BountyEntry, BountyProgress>()

        val entityObjs = data.objectives.content.filterIsInstance<BountyEntryExperience>()

        for (obj in entityObjs) {
            succ[obj] = when (obj.content) {
                "levels" -> BountyProgress(player.experienceLevel to obj.amount)
                "points" -> BountyProgress(player.experienceTotal to obj.amount)
                else -> BountyProgress(1 to 1)
            }
        }

        return succ
    }


}