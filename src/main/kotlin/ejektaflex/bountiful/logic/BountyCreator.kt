package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.ContentRegistry
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.weightedRandom
import ejektaflex.bountiful.api.logic.IBountyCreator
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.*
import kotlin.math.max



object BountyCreator : IBountyCreator {

    private val rand = Random()

    override fun createStack(world: World): ItemStack {
        return ContentRegistry.bounty.let { ItemStack(it).apply { it.ensureBounty(this, world) } }
    }

    override fun calcRarity(): EnumBountyRarity {
        var level = 0
        val chance = Bountiful.config.rarityChance
        for (i in 0 until 3) {
            if (rand.nextFloat() < chance) {
                level += 1
            } else {
                break
            }
        }
        return EnumBountyRarity.getRarityFromInt(level)
    }

    private fun createRandomBounty(inRarity: EnumBountyRarity?): BountyData {


        // Shuffle bounty registry and take a random number of bounty items
        val pickedAlready = mutableListOf<PickableEntry>()

        val toPick = Bountiful.config.bountyAmountRange.random()
        while (pickedAlready.size < toPick) {
            val pool = BountyRegistry.items.filter { it !in pickedAlready }
            val toAdd = pool.weightedRandom
            pickedAlready.add(toAdd)
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
            findRewards(worth).forEach {
                rewards.add(it)
            }

        }
    }

    private fun createPremadeBounty(inRarity: EnumBountyRarity?): BountyData {
        // TODO this
        return BountyData()
    }

    override fun create(inRarity: EnumBountyRarity?): BountyData {
        return if (Bountiful.config.randomBounties) {
            createRandomBounty(inRarity)
        } else {
            createPremadeBounty(inRarity)
        }
    }

    private fun findRewards(n: Int): List<PickedEntryStack> {
        var worthLeft = n
        val toRet = mutableListOf<PickedEntryStack>()
        val picked = mutableListOf<String>()
        var validRewards: List<PickedEntryStack> = RewardRegistry.items.filter { it.amount <= worthLeft && it.content !in picked }.sortedBy { it.amount }

        while (validRewards.isNotEmpty()) {
            val reward = when (Bountiful.config.greedyRewards) {
                true -> validRewards.last()
                false -> validRewards.random()
            }

            val maxNumOfReward = worthLeft / reward.amount
            val worthSated = reward.amount * maxNumOfReward
            worthLeft -= worthSated
            println("For bounty, picked: $reward")
            val rewardClone = PickedEntryStack(PickedEntry(reward.content, maxNumOfReward, reward.nbt))
            println("For bounty, clone is: $rewardClone")
            toRet.add(rewardClone)
            validRewards = RewardRegistry.items.filter { it.amount <= worthLeft && it.contentObj !in picked }.sortedBy { it.amount }
        }

        // If there were no valid rewards, find the cheapest item
        if (toRet.isEmpty()) {
            val lowestWorthItem = RewardRegistry.items.minBy { it.amount }!!
            toRet.add(PickedEntryStack(PickedEntry(lowestWorthItem.content, 1, lowestWorthItem.nbt)))
        }

        return toRet
    }

}