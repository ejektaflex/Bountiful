package io.ejekta.bountiful.data

import io.ejekta.bountiful.bounty.BountyRarity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier

@Serializable
data class BountyModifier(
    override var id: String = "DEFAULT_MODIFIER",
    val type: Type = Type.ENCHANTS,
    override val requires: MutableList<String> = mutableListOf(),
    override val replace: Boolean = false,
    val chance: Double = 1.0,
    val onlyCompatible: Boolean = true,
    val levels: PoolEntry.EntryRange = PoolEntry.EntryRange(10, 20),
    val options: List<@Contextual Identifier> = emptyList(),
    val applicableItems: List<@Contextual Identifier> = emptyList(),
    val applicableTags: List<@Contextual Identifier> = emptyList(),
    val rarityWeightScale: Double = 1.0,
    val valueFlatScale: Double = 30.0,
    val valueCostScale: Double = 0.018
) : IMerge<BountyModifier> {

    enum class Type {
        ENCHANTS
    }

    val clampedChance: Double
        get() = chance.coerceIn(0.0, 1.0)

    fun rarityForWeight(weight: Int): BountyRarity {
        val scaledWeight = weight / rarityWeightScale.coerceAtLeast(0.0001)
        return when {
            scaledWeight <= 3.0 -> BountyRarity.LEGENDARY
            scaledWeight <= 12.0 -> BountyRarity.EPIC
            scaledWeight <= 28.0 -> BountyRarity.RARE
            scaledWeight <= 60.0 -> BountyRarity.UNCOMMON
            else -> BountyRarity.COMMON
        }
    }

    override fun merged(other: BountyModifier): BountyModifier {
        return BountyModifier(
            id = id,
            type = other.type,
            requires = (requires + other.requires).toSet().toMutableList(),
            replace = replace || other.replace,
            chance = other.chance,
            onlyCompatible = other.onlyCompatible,
            levels = other.levels,
            options = if (other.options.isNotEmpty()) other.options else options,
            applicableItems = if (other.applicableItems.isNotEmpty()) other.applicableItems else applicableItems,
            applicableTags = if (other.applicableTags.isNotEmpty()) other.applicableTags else applicableTags,
            rarityWeightScale = other.rarityWeightScale,
            valueFlatScale = other.valueFlatScale,
            valueCostScale = other.valueCostScale
        )
    }
}
