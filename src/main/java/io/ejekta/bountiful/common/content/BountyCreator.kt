package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.BountyData
import io.ejekta.bountiful.common.bounty.BountyDataEntry
import io.ejekta.bountiful.common.config.BountifulIO
import io.ejekta.bountiful.common.config.Decree
import io.ejekta.bountiful.common.config.Pool
import io.ejekta.bountiful.common.config.PoolEntry
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
        return getRewardPoolsFor(decrees).map { it.content }.flatten().filter { it.type.isReward }.toSet()
    }

    private fun getObjectivesFor(decrees: Set<Decree>): Set<PoolEntry> {
        return getObjectivePoolsFor(decrees).map { it.content }.flatten().filter { it.type.isObj }.toSet()
    }

    fun createBounty(decrees: Set<Decree>, rep: Int, startTime: Long = 0L): BountyData {

        val bd = BountyData()

        val worth = createRewards(bd, decrees, rep)

        // return early if we have no rewards :(
        if (bd.rewards.isEmpty()) {
            return bd
        }

        //bd.timeToComplete += bd.rewards.map { it.worth.toLong() }.sum() * 10

        //println("Created rewards worth $worth")

        createObjectives(bd, decrees, rep, worth)

        bd.timeStarted = startTime
        bd.timeToComplete += 15000L + BountifulIO.config.bountyBonusTime

        //bd.timeToComplete = bd.timeToComplete.toDouble().pow(0.95).toLong() // curve off high value items

        return bd
    }

    private fun getObjectivesWithinVariance(objs: List<PoolEntry>, worth: Double, variance: Double): List<PoolEntry> {
        val wRange = ceil(worth * variance)

        // TODO Make sure to filter out non-objectives

        val objGroups = objs.groupBy { it.worthDistanceFrom(worth) }

        val groupsInRange = objGroups.filter { it.key <= wRange }
        val totalObjs = groupsInRange.values.flatten()

        return totalObjs
    }

    private fun pickObjective(data: BountyData, objs: List<PoolEntry>, worth: Double, rep: Int): BountyDataEntry {
        val variance = 0.25
        val inVariance = getObjectivesWithinVariance(objs, worth, variance)

        // Picks a random pool within the variance. If none exist, get the objective with the closest worth distance.
        val picked = if (inVariance.isNotEmpty()) {
            inVariance.weightedRandomDblBy {
                weightMult * rarity.weightAdjustedFor(rep)
            }
        } else {
            //println("Nothing was in variance")
            objs.minByOrNull { it.worthDistanceFrom(worth) }!!
        }

        val entry = picked.toEntry(worth)

        data.timeToComplete += (picked.timeMult * entry.worth).toLong() * 7

        return entry
    }

    fun createObjectives(data: BountyData, decrees: Set<Decree>, rep: Int, worth: Double) {
        // -30 = 150% / 1.5x needed, 30 = 50% / 0.5x needed
        // 1 - (rep / 60.0)
        val objNeededMult = 1 - (rep / 60.0)
        var worthNeeded = worth * objNeededMult
        val numObjectives = (1..2).random()

        //println("Must create objectives that have a worth that adds up to $worth (actually $worthNeeded)")

        val objs = getObjectivesFor(decrees).filter {
            it.content !in data.rewards.map { rew -> rew.content }
        }



        val worthGroups = randomSplit(worthNeeded, numObjectives).toMutableList()

        //println("Split into: $worthGroups")

        while (worthGroups.isNotEmpty()) {
            val w = worthGroups.removeAt(0)

            val alreadyPicked = data.objectives.map { it.content }
            val unpicked = objs.filter { it.content !in alreadyPicked }

            if (unpicked.isEmpty()) {
                println("Ran out of objectives to pick from! Already picked: $alreadyPicked")
                break
            }

            val picked = pickObjective(data, unpicked, w, rep)

            // Append on a new worth to add obj for
            // if we still haven't fulfilled it
            if (picked.worth < w * 0.5) {
                //println("Cannot satisfy all, must append another (${picked.worth}, $w)")
                worthGroups.add(w - picked.worth)
            }

            //println("Picked an item with worth: $w (above)")
            data.objectives.add(picked)


        }


    }


    fun createRewards(data: BountyData, decrees: Set<Decree>, rep: Int): Double {
        val rewards = getRewardsFor(decrees)

        if (rewards.isEmpty()) {
            return 0.0
        }

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
                weightMult * rarity.weightAdjustedFor(rep)
            }

            toReturn.add(picked)
        }

        data.rarity = toReturn.maxOf { it.rarity }


        val worths = toReturn.map { it.unitWorth to it.toEntry() }

        data.rewards.addAll(worths.map { it.second })
        return worths.sumOf { it.first * it.second.amount }
    }





}