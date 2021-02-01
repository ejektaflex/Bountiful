package io.ejekta.bountiful.common.bounty.logic

import net.minecraft.util.Formatting
import kotlin.math.pow

enum class BountyRarity(val color: Formatting, val weight: Int, val repTier: Int) {
    COMMON(Formatting.WHITE, 1024, -30),
    UNCOMMON(Formatting.AQUA, 512, 5),
    RARE(Formatting.YELLOW, 256, 15),
    EPIC(Formatting.LIGHT_PURPLE, 128, 25),
    LEGENDARY(Formatting.GOLD, 64, 30);

    fun weightAt(rep: Int): Double {
        return weight.toDouble() / ((1.5).pow(ordinal))
    }

    companion object {
        fun forReputation(rep: Int): BountyRarity {
            return values().first { it.repTier >= rep }
        }

        fun weightAt(rep: Int) = forReputation(rep).weightAt(rep)
    }

}