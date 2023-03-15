package ejektaflex.bountiful.data.bounty.enums

import net.minecraft.world.item.Rarity

enum class BountyRarity(
        val itemRarity: Rarity,
        val exponent: Double,
        val worthMult: Double,
        val extraRewardChance: Double
        //val trigger: BountifulTrigger
) {

    Common(
            Rarity.COMMON,
            1.0,
            1.0,
            0.0
            //BountifulTriggers.COMPLETE_COMMON
    ),

    Uncommon(
            Rarity.UNCOMMON,
            0.75,
            0.95,
            0.08
            //BountifulTriggers.COMPLETE_UNCOMMON
    ),

    Rare(
            Rarity.RARE,
            0.5,
            0.9,
            0.16
            //BountifulTriggers.COMPLETE_RARE
    ),

    Epic(
            Rarity.EPIC,
            0.25,
            0.82,
            0.24
            //BountifulTriggers.COMPLETE_EPIC
    );

    companion object {

        fun getRarityFromInt(n: Int): BountyRarity {
            return if (n in values().indices) {
                values()[n]
            } else {
                Common
            }
        }
    }
}

