package ejektaflex.bountiful.api.enum

import ejektaflex.bountiful.api.BountifulAPI
import net.minecraft.item.Rarity

enum class EnumBountyRarity(
        val level: Int,
        val itemRarity: Rarity,
        //val stat: BountifulStats.BountifulStat,
        val bountyMult: Double = BountifulAPI.config.rarityMultipliers[level],
        val xp: Int = BountifulAPI.config.xpBonuses[level]
) {

    Common(0, Rarity.COMMON),
    Uncommon(1, Rarity.UNCOMMON),
    Rare(2, Rarity.RARE),
    Epic(3, Rarity.EPIC);

    companion object {
        fun getRarityFromInt(n: Int): EnumBountyRarity {
            return if (n in 0 until values().size) {
                EnumBountyRarity.values()[n]
            } else {
                Common
            }
        }
    }
}

