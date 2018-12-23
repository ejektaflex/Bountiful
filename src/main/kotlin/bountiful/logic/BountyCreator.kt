package bountiful.logic

import bountiful.Bountiful
import bountiful.ContentRegistry
import bountiful.registry.BountyRegistry
import bountiful.registry.RewardRegistry
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import java.util.*
import kotlin.math.min

object BountyCreator {

    private var numBountyItems = (2..2)
    private var numRewardItems = (2..10)

    val rand = Random()

    fun createStack(): ItemStack {
        return ItemStack(ContentRegistry.bounty).apply { ContentRegistry.bounty.ensureBounty(this) }
    }

    enum class BountyRarity(val level: Int, val itemRarity: EnumRarity, val bountyMult: Float) {
        Common(0, EnumRarity.COMMON, 1f),
        Uncommon(1, EnumRarity.UNCOMMON, 1.1f),
        Rare(2, EnumRarity.RARE, 1.2f),
        Epic(3, EnumRarity.EPIC, 1.5f)
    }

    fun getRarityFromInt(n: Int): BountyRarity {
        return BountyRarity.values()[n]
    }

    private fun calcRarity(): BountyRarity {
        var level = 0
        val chance = 0.27f
        for (i in 0 until 3) {
            if (rand.nextFloat() < chance) {
                level += 1
            } else {
                break
            }
        }
        return getRarityFromInt(level)
    }

    fun create(): BountyData {
        // Shuffle bounty registry and take a random number of bounty items
        val itemsToPick = BountyRegistry.items.shuffled().take(numBountyItems.random())
        return BountyData().apply {
            rarity = calcRarity().level
            worth = 0
            // Generate bounty data
            itemsToPick.forEach {
                val amountOfItem = it.randCount
                toGet.add(it.itemStack!! to amountOfItem)
                worth += (amountOfItem * it.unitWorth)
            }

            // Make worth affected by rarity
            worth = (worth * getRarityFromInt(rarity).bountyMult).toInt()

            // Generate rewards based on worth
            newFind(worth).forEach {
                rewards.add(it)
            }

            time = worth * Bountiful.config.timeMultiplier
        }
    }

    fun newFind(n: Int): List<Pair<ItemStack, Int>> {
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

        return toRet
    }

}