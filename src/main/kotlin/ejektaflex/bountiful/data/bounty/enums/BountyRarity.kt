package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.BountifulStats
import net.minecraft.item.Rarity
import net.minecraft.util.ResourceLocation

enum class BountyRarity(
        val level: Int,
        val itemRarity: Rarity,
        val exponent: Double,
        val stat: ResourceLocation
) {

    Common(0, Rarity.COMMON, 1.0, BountifulStats.BOUNTIES_DONE_COMMON),
    Uncommon(1, Rarity.UNCOMMON, 0.75, BountifulStats.BOUNTIES_DONE_UNCOMMON),
    Rare(2, Rarity.RARE, 0.5, BountifulStats.BOUNTIES_DONE_RARE),
    Epic(3, Rarity.EPIC, 0.25, BountifulStats.BOUNTIES_DONE_EPIC);

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

