package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.components.BountyInfo
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.randomSplit
import io.ejekta.bountiful.util.weightedRandomDblBy
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import kotlin.math.ceil

class BountyCreator private constructor(
    private val world: ServerLevel,
    private val pos: BlockPos,
    private val decrees: Set<Decree>,
    private val rep: Int
) {
    private val startTime: Long = world.gameTime

    data class ValuedEntry(val dataEntry: BountyDataEntry, val worth: Double)

    // Handle matching algorithm direction
    private val rewardsFirst = !BountifulIO.configData.bounty.reverseMatchingAlgorithm


    internal val objectives = mutableListOf<ValuedEntry>()
    internal val rewards = mutableListOf<ValuedEntry>()

    private var infoRarity = BountyRarity.COMMON
    private var infoTimeStarted = -1L
    private var infoTimePickedUp = -1L
    private var infoTimeToComplete = -1L
    private var infoTargetRatio: Double? = null

    enum class CreationType(
        val named: String,
        val poolGetter: (Decree) -> List<Pool>,
        val dataGetter: (BountyCreator) -> MutableList<ValuedEntry>,
        val itemFilter: (PoolEntry) -> Boolean
    ) {
        REW("reward", { it.rewardPools }, { it.rewards }, { it.typeLogic is IBountyReward }),
        OBJ("objective", { it.objectivePools }, { it.objectives }, { it.typeLogic is IBountyObjective })
    }

    private fun getCreation(initial: Boolean): CreationType {
        return when (initial) {
            rewardsFirst -> CreationType.REW
            else -> CreationType.OBJ
        }
    }

    val stack: ItemStack by lazy {
        create()
        ItemStack(BountifulContent.BOUNTY_ITEM).apply {
            this[BountifulContent.BOUNTY_OBJS] = objectives.map { it.dataEntry }
            this[BountifulContent.BOUNTY_REWS] = rewards.map { it.dataEntry }

            val objWorths = if (Bountiful.packMode) objectives.map { it.worth.toInt() } else null
            val rewWorths = if (Bountiful.packMode) rewards.map { it.worth.toInt() } else null

            this[BountifulContent.BOUNTY_INFO] = BountyInfo(
                infoRarity,
                infoTimeStarted,
                infoTimeToComplete,
                infoTimePickedUp,
                objWorths = objWorths,
                rewWorths = rewWorths,
                targetRatio = if (Bountiful.packMode) infoTargetRatio else null
            )
        }
    }

    fun create() {
        // Gen reward entries and max rarity
        val initialEntries = genInitialEntries()

        if (initialEntries.isEmpty()) {
            val initialName = getCreation(true).named
            Bountiful.LOGGER.error("${initialName.uppercase()}s are empty, can only generate an empty $initialName")
            return
        }

        val infoRarityOrdinal = initialEntries.maxOf { it.rarity.ordinal }
        infoRarity = BountyRarity.entries[infoRarityOrdinal]

        // Gen rewards and total worth
        val initialPicks = genInitial(initialEntries)
        val totalInitialWorth = initialPicks.sumOf { it.worth }
        getCreation(true).dataGetter(this).addAll(initialPicks)

        // return early if we have no rewards :(
        if (initialPicks.isEmpty()) {
            return
        }

        // Gen filler
        val fillerPicks = genFillers(
            totalInitialWorth * (1 + (BountifulIO.configData.bounty.objectiveDifficultyModifierPercent * 0.01)),
            initialEntries
        )
        getCreation(false).dataGetter(this).addAll(fillerPicks)

        infoTimeStarted = startTime
        infoTimePickedUp = startTime // just for now
        infoTimeToComplete += 750L + BountifulIO.configData.bounty.flatBonusTimePerBountyInSecs
    }

    private fun genInitial(entries: List<PoolEntry>): List<ValuedEntry> {
        return entries.map { it.toEntry(world, pos) }
    }

    private fun genInitialEntries(): List<PoolEntry> {
        val initials = getInitialFor(decrees, world, getCreation(true))

        if (initials.isEmpty()) {
            return emptyList()
        }

        // Num rewards to give
        val numInitials = (1..BountifulIO.configData.bounty.maxNumRewards).random()
        val toReturn = mutableListOf<PoolEntry>()

        for (i in 0 until numInitials) {
            val totalInitials = initials.filter {
                it.content !in toReturn.map { alreadyAdded -> alreadyAdded.content }
                        && rep >= it.repRequired
            }

            // Return if there's nothing to pick
            if (totalInitials.isEmpty()) {
                break
            }

            val picked = totalInitials.weightedRandomDblBy {
                weightMult * rarity.weightAdjustedFor(rep)
            }

            toReturn.add(picked)
        }
        return toReturn
    }

    private fun getAllPossibleFillers(initialPools: List<PoolEntry>): List<PoolEntry> {
        return getEntriesFor(decrees, getCreation(false)).filter {
            it.content !in getCreation(true).dataGetter(this).map { item -> item.dataEntry.content }
        }.filter { entry ->
            // obj entry can not be in any reward forbidlist
            // no rew entry can be in this obj entry's forbidlist either
            !entry.forbidsAny(world, initialPools) && !initialPools.any { it.forbids(world, entry) }
        }.mapNotNull {
            val entryIsValid = it.isValid(world.server)
            if (!entryIsValid) {
                Bountiful.LOGGER.warn("Bountiful ${getCreation(false).name} pool entry is not valid!: ${it.id}")
            }
            it.takeIf { entryIsValid } // Only use valid pool entries
        }
    }

    private fun genFillers(worth: Double, initialPools: List<PoolEntry>): List<ValuedEntry> {
        // -30 = 150% / 1.5x needed, 30 = 50% / 0.5x needed
        // 1 - (rep / 60.0)
        val fillerNeededMult = getDiscount(rep)
        infoTargetRatio = fillerNeededMult

        val worthNeeded = if (rewardsFirst) worth * fillerNeededMult else worth / fillerNeededMult // When reversed, generated rewards should be that mult amount bigger by dividing

        val numFillers = BountifulIO.configData.bounty.matchCountPreference.pick()

        val toReturn = mutableListOf<ValuedEntry>()

        val fills = getAllPossibleFillers(initialPools)

        val worthGroups = randomSplit(worthNeeded, numFillers).toMutableList()

        while (worthGroups.isNotEmpty()) {
            val w = worthGroups.removeAt(0)

            val alreadyPicked = toReturn.map { it.dataEntry.content }
            val unpicked = fills.filter { it.content !in alreadyPicked }

            if (unpicked.isEmpty()) {
                //println("Ran out of objectives to pick from! Already picked: $alreadyPicked")
                break
            }

            val picked = pickFiller(unpicked, w)
            val entry = picked.toEntry(world, pos, w, decrees.map { it.id }.toSet())

            // Add time based on entry
            infoTimeToComplete += (picked.timeMult * entry.worth * 0.35).toLong()

            // Append on a new worth to add obj for
            // if we still haven't fulfilled it
            // think of this as the "emergency foot-shooting escape plan"
            if (entry.worth < w * 0.5) {
                worthGroups.add(w - entry.worth)
            }

            toReturn.add(entry)
        }

        return toReturn
    }

    private fun pickFiller(fillers: List<PoolEntry>, worth: Double): PoolEntry {
        val variance = 0.25
        val inVariance = getFillersWithinVariance(fillers, worth, variance)

        // Picks a random pool within the variance. If none exist, get the objective with the closest worth distance.
        val picked = if (inVariance.isNotEmpty()) {
            inVariance.weightedRandomDblBy {
                weightMult * rarity.weightAdjustedFor(rep)
            }
        } else {
            fillers.minByOrNull { it.worthDistanceFrom(worth) }!!
        }

        return picked
    }

    companion object {
        // discount is 40% at rep 30, approaches 66% as you increase beyond 30 towards infinity
        // bounties needed to get rep as a function f(x), where input x is rep:
        // f(x) = 0.1*(x^2) + 1.5x
        fun getDiscount(rep: Int): Double {
            val baseDiscount = (rep.coerceIn(-30..30) / 75.0) //=> 30 levels * 1.33% = 40%
            val hiTier = (if (rep > 30) {
                (rep - 30) / (rep - 30 + 80.0) // number added at the end determines how fast we approach 66%
            } else { 0.0 }) * ((2 / 3.0) - 0.4) // forever approaching 66% discount
            return 1 - (baseDiscount + hiTier)
        }

        fun createBountyItem(world: ServerLevel, pos: BlockPos, decrees: Set<Decree>, rep: Int): ItemStack {
            return BountyCreator(world, pos, decrees, rep).stack
        }

        fun getPoolsFor(decrees: Set<Decree>, creationType: CreationType): Set<Pool> {
            return decrees.map(creationType.poolGetter).flatten().toSet()
        }

        private fun getInitialFor(decrees: Set<Decree>, world: ServerLevel, creationType: CreationType): Set<PoolEntry> {
            return getPoolsFor(decrees, creationType).asSequence().map { it.items }.flatten().filter(creationType.itemFilter).mapNotNull {
                val entryIsValid = it.isValid(world.server)
                if (!entryIsValid) {
                    Bountiful.LOGGER.warn("Bountiful reward pool entry is not valid!: ${it.id}")
                }
                it.takeIf { entryIsValid } // Only use valid pool entries
            }.toSet()
        }

        private fun getEntriesFor(decrees: Set<Decree>, creationType: CreationType): Set<PoolEntry> {
            return getPoolsFor(decrees, creationType).map { it.items }.flatten().filter(creationType.itemFilter).toSet()
        }

        private fun getFillersWithinVariance(objs: List<PoolEntry>, worth: Double, variance: Double): List<PoolEntry> {
            val wRange = ceil(worth * variance)
            val entryGroups = objs.groupBy { it.worthDistanceFrom(worth) }
            val groupsInRange = entryGroups.filter { it.key <= wRange }.values
            return groupsInRange.flatten()
        }

    }

}