package ejektaflex.bountiful.api.enum

import net.minecraft.item.EnumRarity

enum class EnumBountyRarity(val level: Int, val itemRarity: EnumRarity, val bountyMult: Float) {
    Common(0, EnumRarity.COMMON, 1f),
    Uncommon(1, EnumRarity.UNCOMMON, 1.1f),
    Rare(2, EnumRarity.RARE, 1.2f),
    Epic(3, EnumRarity.EPIC, 1.5f);

    companion object {
        fun getRarityFromInt(n: Int): EnumBountyRarity {
            return EnumBountyRarity.values()[n]
        }
    }
}

