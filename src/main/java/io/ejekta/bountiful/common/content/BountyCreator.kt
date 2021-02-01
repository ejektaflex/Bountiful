package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.data.pool.Decree
import io.ejekta.bountiful.common.bounty.data.pool.Pool
import io.ejekta.bountiful.common.bounty.data.pool.PoolEntry
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyDataEntry
import io.ejekta.bountiful.common.util.randomSplit
import io.ejekta.bountiful.common.util.weightedRandomDblBy
import kotlin.math.ceil

object BountyCreator {

    private fun getObjectivePoolsFor(decrees: Set<Decree>): Set<Pool> {
        return decrees.map { it.objectivePools }.flatten().toSet()
    }

    private fun getRewardPoolsFor(decrees: Set<Decree>): Set<Pool> {
        return decrees.map { it.rewardPools }.flatten().toSet()
    }

    private fun getRewardsFor(decrees: Set<Decree>): Set<PoolEntry> {
        return getRewardPoolsFor(decrees).map { it.content }.flatten().toSet()
    }

    private fun getObjectivesFor(decrees: Set<Decree>): Set<PoolEntry> {
        return getObjectivePoolsFor(decrees).map { it.content }.flatten().toSet()
    }

    fun createBounty(decrees: Set<Decree>, rep: Int): BountyData {

        val bd = BountyData()

        val worth = createRewards(bd, decrees, rep)

        println("Created rewards worth $worth")

        createObjectives(bd, decrees, rep, worth)

        println("Final bounty: $bd")

        return bd
    }

    fun getObjectivesWithinVariance(objs: List<PoolEntry>, worth: Double, variance: Double): List<PoolEntry> {
        val wRange = ceil(worth * variance)

        // TODO Make sure to filter out non-objectives
        val objGroups = objs.groupBy { it.worthDistanceFrom(worth) }

        val groupsInRange = objGroups.filter { it.key <= wRange }
        val totalObjs = groupsInRange.values.flatten()

        return totalObjs
    }

    fun pickObjective(objs: List<PoolEntry>, worth: Double): BountyDataEntry {
        val variance = 0.2
        val inVariance = getObjectivesWithinVariance(objs, worth, variance)

        // Picks a random pool within the variance. If none exist, get the objective with the closest worth distance.
        val pickedPool = if (inVariance.isNotEmpty()) {
            inVariance.random()
        } else {
            println("Nothing was in variance")
            objs.minByOrNull { it.worthDistanceFrom(worth) }!!
        }
        return pickedPool.toEntry(worth).also { println("picked $it") }
    }

    fun createObjectives(data: BountyData, decrees: Set<Decree>, rep: Int, worth: Double) {
        // -30 = 150% / 1.5x needed, 30 = 50% / 0.5x needed
        // 1 - (rep / 60.0)
        val objNeededMult = 1 - (rep / 60.0)
        var worthNeeded = worth * objNeededMult
        val numObjectives = (1..2).random()

        println("Must create objectives that have a worth that adds up to $worth (actually $worthNeeded)")

        val objs = getObjectivesFor(decrees).filter {
            it.content !in data.rewards.map { rew -> rew.content }
        }

        val worthGroups = randomSplit(worthNeeded, numObjectives)

        println("Split into: $worthGroups")

        for (w in worthGroups) {
            val picked = pickObjective(objs, w)
            println("Picked an item with worth: $w (above)")
            data.objectives.add(picked)
        }

    }


    fun createRewards(data: BountyData, decrees: Set<Decree>, rep: Int): Double {
        val rewards = getRewardsFor(decrees)
        // Num rewards to give
        val numRewards = (1..2).random()
        val toReturn = mutableListOf<PoolEntry>()

        for (i in 0 until numRewards) {
            val totalRewards = rewards.filter {
                it.content !in toReturn.map { alreadyAdded -> alreadyAdded.content }
                        && rep >= it.repRequired
            }

            // Return if there's nothing to pick
            if (totalRewards.isEmpty()) {
                break
            }

            val picked = totalRewards.weightedRandomDblBy {
                weightMult * rarity.weightAt(rep) * timeMult * repMult
            }

            toReturn.add(picked)
        }

        data.rarity = toReturn.maxOf { it.rarity }


        val worths = toReturn.map { it.unitWorth to it.toEntry() }

        data.rewards.addAll(worths.map { it.second })
        return worths.sumOf { it.first * it.second.amount }
    }





}