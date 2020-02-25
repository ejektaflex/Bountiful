package ejektaflex.bountiful.logic

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.hackyRandom
import ejektaflex.bountiful.api.ext.randomSplit
import ejektaflex.bountiful.api.ext.weightedRandom
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.*
import kotlin.math.ceil


object BountyCreator : IBountyCreator {

    private val rand = Random()

    override fun createStack(world: World, rarity: EnumBountyRarity?): ItemStack {
        return ItemStack.EMPTY
        //return ContentRegistry.bounty.let { ItemStack(it).apply { it.ensureBounty(this, world, rarity) } }
    }

    override fun calcRarity(): EnumBountyRarity {
        var level = 0
        val chance = BountifulMod.config.rarityChance
        for (i in 0 until 3) {
            if (rand.nextFloat() < chance) {
                level += 1
            } else {
                break
            }
        }
        return EnumBountyRarity.getRarityFromInt(level)
    }

    override fun create(world: World, inRarity: EnumBountyRarity, decrees: List<IDecree>): BountyData {
        val data = BountyData()

        println("Yep, that's a bounty!")
        println(DecreeRegistry.content.size)
        println(PoolRegistry.content.size)

        println("Decrees: $decrees")

        val toSatisfy = createRewards(data, world, inRarity, decrees)
        createObjectives(data, world, inRarity, decrees, toSatisfy)

        //println(JsonAdapter.toJson(data, BountyData::class))

        return data
    }

    private fun createRewards(data: BountyData, world: World, inRarity: EnumBountyRarity, decrees: List<IDecree>): Int {

        val rewards = DecreeRegistry.getRewards(decrees)

        val numRewards = (1..2).hackyRandom()

        val rarity = (0..3).hackyRandom()

        val toAdd = mutableListOf<BountyEntry>()

        for (i in 0 until numRewards) {

            val totalRewards = rewards.filter {
                it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
            }

            // Return if there's nothing to pick
            if (totalRewards.isEmpty()) {
                break
            }

            toAdd.add( totalRewards.weightedRandom.pick() )
        }

        val accumWorth = toAdd.sumBy { it.calculatedWorth }

        data.rarity = rarity
        data.rewards.add(*toAdd.toTypedArray())

        return accumWorth

    }

    private fun createObjectives(data: BountyData, world: World, inRarity: EnumBountyRarity, decrees: List<IDecree>, worth: Int) {

        val objectives = DecreeRegistry.getObjectives(decrees)
        val numObjectives = (1..2).hackyRandom()
        val worthGroups = randomSplit(worth, numObjectives)

        if (objectives.isEmpty()) {
            return
        }

        val variance = 0.1

        val toAdd = mutableListOf<BountyEntry>()

        for (wrth in worthGroups) {
            val wRange = ceil(wrth * variance)

            // Filter out things already picked
            val totalObjectives = objectives.filter {
                it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
            }

            // Return if there's nothing to pick
            if (totalObjectives.isEmpty()) {
                break
            }

            // Make sure to filter out non-objectives
            val objGroups = totalObjectives.groupBy { it.worthDistanceFrom(wrth) }

            println("Obj groups keys: " + objGroups.keys.toString())

            val groupsInRange = objGroups.filter { it.key <= wRange }
            val totalObjs = groupsInRange.values.flatten()

            // If there are no objectives within variance from target worth, just get the one with the smallest distance
            // Otherwise, if one/some exist, pick at random.
            val closest = if (totalObjs.isEmpty()) {
                val smallestDist = objGroups.keys.min()
                objGroups[smallestDist]!!.hackyRandom().pick(wrth)
            } else {
                totalObjs.hackyRandom().pick(wrth)
            }

            println("Closest for wrth [$wrth]: $closest")

            toAdd.add(closest)
        }


        data.objectives.add(*toAdd.toTypedArray())


    }



}