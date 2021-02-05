package io.ejekta.bountiful.common.bounty.logic

import net.minecraft.util.Formatting
import kotlin.math.max
import kotlin.math.pow

enum class BountyRarity(val color: Formatting, val weight: Int, val repTier: Int) {
    COMMON(Formatting.WHITE, 1024, -30),
    UNCOMMON(Formatting.AQUA, 512, 5),
    RARE(Formatting.YELLOW, 256, 15),
    EPIC(Formatting.LIGHT_PURPLE, 128, 25),
    LEGENDARY(Formatting.GOLD, 16, 30);

    fun weightAdjustedFor(currRarity: BountyRarity): Double {
        return weight.toDouble() / (rarityWeightScaling.pow(max(currRarity.ordinal - ordinal, 0)))
    }

    fun weightAdjustedFor(rep: Int) = weightAdjustedFor(forReputation(rep))

    companion object {
        fun forReputation(rep: Int): BountyRarity {
            return values().last { rep >= it.repTier  }
        }

        val rarityWeightScaling = 2.5

        //fun weightAdjustedFor(rep: Int) =
    }

}