package ejektaflex.bountiful.api.enum

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.BountifulAPI
import net.minecraft.item.Rarity

enum class EnumBountyRarity(
        val level: Int,
        val itemRarity: Rarity,
        val exponent: Double,
        //val stat: BountifulStats.BountifulStat,
        val bountyMult: Double = BountifulMod.config.rarityMultipliers[level],
        val xp: Int = BountifulMod.config.xpBonuses[level]
) {

    Common(0, Rarity.COMMON, 1.0),
    Uncommon(1, Rarity.UNCOMMON, 0.75),
    Rare(2, Rarity.RARE, 0.5),
    Epic(3, Rarity.EPIC, 0.25);

    companion object {
        fun getRarityFromInt(n: Int): EnumBountyRarity {
            return if (n in values().indices) {
                values()[n]
            } else {
                Common
            }
        }
    }
}

