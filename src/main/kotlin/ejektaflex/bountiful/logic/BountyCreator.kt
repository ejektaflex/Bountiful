package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.ContentRegistry
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.logic.IBountyCreator
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.item.ItemStack
import java.util.*
import kotlin.math.max



object BountyCreator : IBountyCreator {

    private var numBountyItems = Bountiful.config.bountyAmountRange

    val rand = Random()

    override fun createStack(): ItemStack {
        return ContentRegistry.bounty.let { ItemStack(it).apply { it.ensureBounty(this) } }
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

    override fun create(): BountyData {
        // Shuffle bounty registry and take a random number of bounty items
        val itemsToPick = BountyRegistry.items.shuffled().take(numBountyItems.random())
        return BountyData().apply {
            rarity = calcRarity().level
            worth = 0
            // Generate bounty data
            itemsToPick.forEach {
                val amountOfItem = it.randCount
                val itemItself = it.itemStack
                if (itemItself != null) {
                    toGet.add(it.itemStack!! to amountOfItem)
                    worth += (amountOfItem * it.unitWorth)
                } else {
                    throw BountyCreationException("You tried to create a bounty but the item was invalid!")
                }
            }

            time = max((worth * Bountiful.config.timeMultiplier).toLong(), Bountiful.config.bountyTimeMin.toLong())

            // Make worth affected by rarity
            worth = (worth * EnumBountyRarity.getRarityFromInt(rarity).bountyMult).toInt()

            // Generate rewards based on worth
            findRewards(worth).forEach {
                rewards.add(it)
            }

        }
    }

    private fun findRewards(n: Int): List<Pair<ItemStack, Int>> {
        var worthLeft = n
        val toRet = mutableListOf<Pair<ItemStack, Int>>()
        val picked = mutableListOf<String>()
        var validRewards: List<PickableEntry> = RewardRegistry.items.filter { it.amount.min * it.unitWorth <= worthLeft && it.itemString !in picked }.sortedBy { it.unitWorth }

        while (validRewards.isNotEmpty()) {
            val reward = validRewards.last()
            val maxNumOfReward = worthLeft / reward.unitWorth
            val worthSated = reward.unitWorth * maxNumOfReward
            worthLeft -= worthSated
            toRet.add(reward.itemStack!! to maxNumOfReward)
            validRewards = RewardRegistry.items.filter { it.unitWorth <= worthLeft && it.itemString !in picked }.sortedBy { it.unitWorth }
        }

        // If there were no valid rewards, find the cheapest item
        if (toRet.isEmpty()) {
            val lowestWorthItem = RewardRegistry.items.minBy { it.unitWorth * it.amount.min }!!
            toRet.add(lowestWorthItem.itemStack!! to lowestWorthItem.amount.min)
        }

        return toRet
    }

}