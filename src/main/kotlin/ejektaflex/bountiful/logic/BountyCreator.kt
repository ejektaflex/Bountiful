package ejektaflex.bountiful.logic

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.clampTo
import ejektaflex.bountiful.api.ext.weightedRandom
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.item.ItemBounty
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

    private fun precheckRegistries(world: World) {
        /*
        if (RewardRegistry.validRewards(world).isEmpty()) {
            throw ItemBounty.BountyCreationException("There are no valid rewards in the reward registry!")
        }
        if (BountyRegistry.validBounties(world).size < Bountiful.config.bountyAmountRange.last) {
            throw ItemBounty.BountyCreationException("There are not enough valid bounties in the bounty registry! (At least ${Bountiful.config.bountyAmountRange} must be valid at any one time).")
        }

         */
    }

    private fun createRandomBounty(world: World, inRarity: EnumBountyRarity?): BountyData {
        // Throw exception if we can't complete the bounty
        precheckRegistries(world)

        return BountyData()

        /*

        // Shuffle bounty registry and take a random number of bounty items
        val pickedAlready = mutableListOf<PickableEntry>()

        val toPick = Bountiful.config.bountyAmountRange.random()
        while (pickedAlready.size < toPick) {
            val pool = BountyRegistry.validBounties(world).filter { it !in pickedAlready }
            val toAdd = pool.weightedRandom
            pickedAlready.add(toAdd.copy())
        }

        return BountyData().apply {
            rarity = inRarity?.level ?: calcRarity().level
            worth = 0
            var preBountyTime = 0.0
            // Generate bounty data
            pickedAlready.forEach {
                val picked = it.pick()
                if (picked.contentObj != null) {
                    toGet.add(picked)
                    worth += (picked.amount * it.unitWorth)
                    preBountyTime += (picked.amount * it.unitWorth * picked.timeMult())
                } else {
                    throw BountyCreationException("You tried to create a bounty but the item was invalid! Item was: ${picked.content}")
                }
            }

            bountyTime = max((preBountyTime * Bountiful.config.timeMultiplier).toLong(), Bountiful.config.bountyTimeMin.toLong())

            // Make worth affected by rarity
            worth = (worth * EnumBountyRarity.getRarityFromInt(rarity).bountyMult).toInt()

            // Generate rewards based on worth
            findRewards(world, worth).forEach {
                rewards.add(it)
            }

        }

         */
    }

    private fun createPremadeBounty(inRarity: EnumBountyRarity?): BountyData {
        // TODO this
        return BountyData()
    }

    override fun create(world: World, inRarity: EnumBountyRarity?): BountyData? {
        return if (BountifulMod.config.randomBounties) {
            createRandomBounty(world, inRarity)
        } else {
            createPremadeBounty(inRarity)
        }
    }

    private fun findRewards(world: World, n: Int): List<BountyEntryStack> {
        var worthLeft = n
        val toRet = mutableListOf<BountyEntryStack>()
        val picked = mutableListOf<String>()
        //var validRewards: List<BountyEntryStack> = RewardRegistry.validRewards(world, worthLeft, picked)
        var validRewards: List<BountyEntryStack> = listOf()

        while (validRewards.isNotEmpty()) {
            val reward = when (BountifulMod.config.greedyRewards) {
                true -> validRewards.last()
                false -> validRewards.weightedRandom
            }

            val maxNumCouldGive = (worthLeft / reward.amount)
            val numCanGive = maxNumCouldGive.let {
                1 // just to work
                /*
                val minMaxRange = reward.genericPick.range
                if (minMaxRange != null) {
                    it.clampTo(minMaxRange.toIntRange())
                } else {
                    it
                }

                 */
            }

            val worthSated = reward.amount * numCanGive
            worthLeft -= worthSated
            //val rewardClone = PickedEntryStack(PickedEntry(reward.content, numCanGive, nbtJson = reward.tag?.toString(), stages = reward.stages))
            /*
            picked.add(rewardClone.content) // Don't show up again!
            toRet.add(rewardClone)
            validRewards = RewardRegistry.validRewards(world, worthLeft, picked)

             */
        }

        // If there were no valid rewards, find the cheapest item and give them that.
        if (toRet.isEmpty()) {
            //val lowestWorthItem = RewardRegistry.validRewards(world).minBy { it.amount }!!
            //toRet.add(PickedEntryStack(PickedEntry(lowestWorthItem.content, lowestWorthItem.genericPick.range?.min ?: 1, nbtJson = lowestWorthItem.tag?.toString())))
        }

        return toRet
    }

}