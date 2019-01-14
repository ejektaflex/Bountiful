package ejektaflex.bountiful.api.enum

import ejektaflex.bountiful.api.BountifulAPI
import ejektaflex.bountiful.api.stats.BountifulStats
import net.minecraft.item.EnumRarity

enum class EnumBountyRarity(val level: Int, val itemRarity: EnumRarity, val bountyMult: Double, val stat: BountifulStats.BountifulStat) {
    Common(0, EnumRarity.COMMON, BountifulAPI.config.rarityMultipliers[0], BountifulStats.bountiesCommon),
    Uncommon(1, EnumRarity.UNCOMMON, BountifulAPI.config.rarityMultipliers[1], BountifulStats.bountiesUncommon),
    Rare(2, EnumRarity.RARE, BountifulAPI.config.rarityMultipliers[2], BountifulStats.bountiesRare),
    Epic(3, EnumRarity.EPIC, BountifulAPI.config.rarityMultipliers[3], BountifulStats.bountiesEpic);

    companion object {
        fun getRarityFromInt(n: Int): EnumBountyRarity {
            return EnumBountyRarity.values()[n]
        }
    }
}

