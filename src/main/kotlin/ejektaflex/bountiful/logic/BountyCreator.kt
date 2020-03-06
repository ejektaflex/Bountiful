package ejektaflex.bountiful.logic

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.ext.hackyRandom
import ejektaflex.bountiful.ext.randomSplit
import ejektaflex.bountiful.ext.supposedlyNotNull
import ejektaflex.bountiful.ext.weightedRandomNorm
import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.data.registry.DecreeRegistry
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import java.util.*
import kotlin.math.ceil
import kotlin.math.max


object BountyCreator {

    private val rand = Random()


    fun createStack(world: World, decrees: List<Decree>): ItemStack {

        return ItemStack(BountifulContent.Items.BOUNTY).apply {
            (BountifulContent.Items.BOUNTY as ItemBounty).ensureBounty(this, world, decrees, calcRarity())
        }
        //return ContentRegistry.bounty.let { ItemStack(it).apply { it.ensureBounty(this, world, rarity) } }
    }

    fun calcRarity(): BountyRarity {
        var level = 0
        val chance = BountifulConfig.SERVER.rarityChance.get()
        for (i in 0 until 3) {
            if (rand.nextFloat() < chance) {
                level += 1
            } else {
                break
            }
        }
        return BountyRarity.getRarityFromInt(level)
    }

    fun create(inRarity: BountyRarity, decrees: List<Decree>): BountyData {
        val data = BountyData()

        val toSatisfy = createRewards(data, inRarity, decrees)
        createObjectives(data, inRarity, decrees, toSatisfy)

        return data
    }

    fun createRewards(inRarity: BountyRarity, decrees: List<Decree>): List<BountyEntry> {
        val rewards = DecreeRegistry.getRewards(decrees)

        var numRewards = (1..2).hackyRandom()

        val toAdd = mutableListOf<BountyEntry>()

        for (i in 0 until numRewards) {

            val totalRewards = rewards.filter {
                it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
            }

            // Return if there's nothing to pick
            if (totalRewards.isEmpty()) {
                break
            }

            toAdd.add( totalRewards.weightedRandomNorm(inRarity.exponent).pick() )
        }

        return toAdd
    }


    private fun createRewards(data: BountyData, inRarity: BountyRarity, decrees: List<Decree>): Int {
        val toAdd = createRewards(inRarity, decrees)
        val rarity = BountyRarity.values().indexOf(inRarity)
        data.rarity = rarity
        data.rewards.add(*toAdd.toTypedArray())
        return toAdd.sumBy { it.calculatedWorth }
    }


    val rando = Random()

    /*

        val highestObjWorth = objectives.maxBy { it.maxWorth }

        // Add more objectives if this bounty still couldn't ever possibly hit max worth
        // Later on we should sample for this

        if (highestObjWorth != null) {
            val neededOfThese = ceil(worth.toDouble() / highestObjWorth.maxWorth).toInt()
            if (numObjectives < neededOfThese) {
                numObjectives = neededOfThese
            }
        }
     */

    fun getObjectivesWithinVariance(objs: List<BountyEntry>, worth: Int, variance: Double): List<BountyEntry> {
        val wRange = ceil(worth * variance)

        // Make sure to filter out non-objectives
        val objGroups = objs.groupBy { it.worthDistanceFrom(worth) }

        //println("Obj groups keys: " + objGroups.keys.toString())

        val groupsInRange = objGroups.filter { it.key <= wRange }
        val totalObjs = groupsInRange.values.flatten()

        return totalObjs
    }


    fun pickObjective(objectives: NonNullList<BountyEntry>, worth: Int): BountyEntry {

        val variance = 0.2

        val inVariance = getObjectivesWithinVariance(objectives, worth, variance)

        // If there are no objectives within variance from target worth, just get the one with the smallest distance
        // Otherwise, if one/some exist, pick at random.
        return if (inVariance.isEmpty()) {
            objectives.minBy { it.worthDistanceFrom(worth) }!!.pick(worth)
        } else {
            inVariance.hackyRandom().pick(worth)
        }
    }

    fun createObjectives(rewards: List<BountyEntry>, inRarity: BountyRarity, decrees: List<Decree>, worth: Int): List<BountyEntry> {
        val rewardContentIds = rewards.map { it.content }

        val objectives = DecreeRegistry.getObjectives(decrees).filter {
            it.content !in rewardContentIds
        }

        var numObjectives = (1..2).hackyRandom()

        /*
        // Possible chance for higher tier bounties to get an additional objective
        var chanceToAddThirdObj = (1.0 - inRarity.exponent) / 2
        if (rando.nextFloat() < chanceToAddThirdObj) {
            numObjectives++
        }

         */

        if (objectives.isEmpty()) {
            return listOf()
        }

        val worthGroups = randomSplit(worth, numObjectives)

        val toAdd = mutableListOf<BountyEntry>()

        for (wrth in worthGroups) {

            // Filter out things already picked
            val pickableObjs = objectives.filter {
                it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
            }

            // Return if there's nothing to pick
            if (pickableObjs.isEmpty()) {
                break
            }

            val closest = pickObjective(supposedlyNotNull(pickableObjs), wrth)

            toAdd.add(closest)
        }

        return toAdd
    }

    private fun createObjectives(data: BountyData, inRarity: BountyRarity, decrees: List<Decree>, worth: Int) {
        val objs = createObjectives(data.rewards.content, inRarity, decrees, worth)

        for (obj in objs) {
            data.bountyTime += if (obj.timeMult != null) {
                (worth * BountifulConfig.SERVER.timeMultiplier.get() * obj.timeMult!!).toLong()
            } else {
                (worth * BountifulConfig.SERVER.timeMultiplier.get()).toLong()
            }
        }

        data.bountyTime = max(data.bountyTime, BountifulConfig.SERVER.bountyTimeMin.get().toLong() * 20)

        data.objectives.add(*objs.toTypedArray())
    }



}