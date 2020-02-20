package ejektaflex.bountiful.logic

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.clampTo
import ejektaflex.bountiful.api.ext.weightedRandom
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.*
import kotlin.math.max



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
        val objectives = DecreeRegistry.getObjectives(decrees)

        createRewards(data, world, inRarity, decrees)

        return data
    }

    fun createRewards(data: BountyData, world: World, inRarity: EnumBountyRarity, decrees: List<IDecree>) {

        println("Yep, that's a bounty!")
        println(DecreeRegistry.content.size)
        println(PoolRegistry.content.size)

        val rewards = DecreeRegistry.getRewards(decrees).sortedBy { it.averageWorth }

        println("Decrees: $decrees")
        println("Rewards: $rewards")
        println("Num rewards: ${rewards.size}")



        val randReward = rewards.weightedRandom

        data.rewards.add(randReward)

        //data.rewards.add(randReward)


        println(JsonAdapter.toJson(data, BountyData::class))


    }



}