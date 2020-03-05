package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.BountifulMod
import net.minecraft.item.Rarity

enum class BountyRarity(
        val level: Int,
        val itemRarity: Rarity,
        val exponent: Double
) {

    Common(0, Rarity.COMMON, 1.0),
    Uncommon(1, Rarity.UNCOMMON, 0.75),
    Rare(2, Rarity.RARE, 0.5),
    Epic(3, Rarity.EPIC, 0.25);

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

