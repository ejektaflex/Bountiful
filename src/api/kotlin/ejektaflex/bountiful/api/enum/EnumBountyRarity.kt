package ejektaflex.bountiful.api.enum

import ejektaflex.bountiful.api.BountifulAPI
import net.minecraft.item.EnumRarity

enum class EnumBountyRarity(val level: Int, val itemRarity: EnumRarity, val bountyMult: Double) {
    Common(0, EnumRarity.COMMON, BountifulAPI.config.rarityMultipliers[0]),
    Uncommon(1, EnumRarity.UNCOMMON, BountifulAPI.config.rarityMultipliers[1]),
    Rare(2, EnumRarity.RARE, BountifulAPI.config.rarityMultipliers[2]),
    Epic(3, EnumRarity.EPIC, BountifulAPI.config.rarityMultipliers[3]);

    companion object {
        fun getRarityFromInt(n: Int): EnumBountyRarity {
            return EnumBountyRarity.values()[n]
        }
    }
}

