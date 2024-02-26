package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.randomSplit
import io.ejekta.bountiful.util.weightedRandomDblBy
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import kotlin.math.ceil

class BountyCreator private constructor(
    private val world: ServerWorld,
    private val pos: BlockPos,
    private val decrees: Set<Decree>,
    private val rep: Int,
    private val startTime: Long = 0L,
) {
    // Handle matching algorithm direction
    private val rewardsFirst = !BountifulIO.configData.bounty.reverseMatchingAlgorithm

    private var data = BountyData()
    private var info = BountyInfo()

    enum class CreationType(
        val named: String,
        val poolGetter: (Decree) -> List<Pool>,
        val dataGetter: (BountyData) -> MutableList<BountyDataEntry>,
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
            BountyData[this] = data
            BountyInfo[this] = info
        }
    }

    fun create(): Pair<BountyData, BountyInfo> {
        //data = BountyData()

        // Gen reward entries and max rarity
        val initialEntries = genInitialEntries()

        if (initialEntries.isEmpty()) {
            val initialName = getCreation(true).named
            Bountiful.LOGGER.error("${initialName.uppercase()}s are empty, can only generate an empty $initialName")
            return data to info
        }

        info.rarity = initialEntries.maxOf { it.rarity }

        // Gen rewards and total worth
        val initialPicks = genInitial(initialEntries)
        val totalInitialWorth = initialPicks.sumOf { it.worth }
        getCreation(true).dataGetter(data).addAll(initialPicks)

        // return early if we have no rewards :(
        if (initialPicks.isEmpty()) {
            return data to info
        }

        // Gen filler
        val fillerPicks = genFillers(
            totalInitialWorth * (1 + (BountifulIO.configData.bounty.objectiveDifficultyModifierPercent * 0.01)),
            initialEntries
        )
        getCreation(false).dataGetter(data).addAll(fillerPicks)

        info.timeStarted = startTime
        info.timePickedUp = startTime // just for now
        info.timeToComplete += 750L + BountifulIO.configData.bounty.flatBonusTimePerBountyInSecs


        return data to info
    }

    private fun genInitial(entries: List<PoolEntry>): List<BountyDataEntry> {
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
            it.content !in getCreation(true).dataGetter(data).map { item -> item.content }
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

    private fun genFillers(worth: Double, initialPools: List<PoolEntry>): List<BountyDataEntry> {
        // -30 = 150% / 1.5x needed, 30 = 50% / 0.5x needed
        // 1 - (rep / 60.0)
        val fillerNeededMult = getDiscount(rep)

        val worthNeeded = if (rewardsFirst) worth * fillerNeededMult else worth / fillerNeededMult // When reversed, generated rewards should be that mult amount bigger by dividing
        val numFillers = (1..2).random()
        val toReturn = mutableListOf<BountyDataEntry>()

        val fills = getAllPossibleFillers(initialPools)

        val worthGroups = randomSplit(worthNeeded, numFillers).toMutableList()

        while (worthGroups.isNotEmpty()) {
            val w = worthGroups.removeAt(0)

            val alreadyPicked = toReturn.map { it.content }
            val unpicked = fills.filter { it.content !in alreadyPicked }

            if (unpicked.isEmpty()) {
                //println("Ran out of objectives to pick from! Already picked: $alreadyPicked")
                break
            }

            val picked = pickFiller(unpicked, w)
            val entry = picked.toEntry(world, pos, w, decrees.map { it.id }.toSet())

            // Add time based on entry
            info.timeToComplete += (picked.timeMult * entry.worth * 0.35).toLong()

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

        // cap rep discount at 30/75, or 40%
        fun getDiscount(rep: Int): Double {
            return 1 - (rep.coerceIn(-30..30) / 75.0)
        }

        fun createBountyItem(world: ServerWorld, pos: BlockPos, decrees: Set<Decree>, rep: Int, startTime: Long = 0L): ItemStack {
            return BountyCreator(world, pos, decrees, rep.coerceIn(-30..30), startTime).stack
        }

        fun getPoolsFor(decrees: Set<Decree>, creationType: CreationType): Set<Pool> {
            return decrees.map(creationType.poolGetter).flatten().toSet()
        }

        private fun getInitialFor(decrees: Set<Decree>, world: ServerWorld, creationType: CreationType): Set<PoolEntry> {
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