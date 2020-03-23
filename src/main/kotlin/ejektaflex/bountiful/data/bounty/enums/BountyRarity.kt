package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.BountifulStats
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Rarity
import net.minecraft.stats.Stats
import net.minecraft.util.ResourceLocation

enum class BountyRarity(
        val itemRarity: Rarity,
        val exponent: Double,
        val stat: ResourceLocation,//,
        val worthMult: Double,
        val extraRewardChance: Double
        //val trigger: BountifulTrigger
) {

    Common(
            Rarity.COMMON,
            1.0,
            BountifulStats.BOUNTIES_DONE_COMMON,
            1.0,
            0.0
            //BountifulTriggers.COMPLETE_COMMON
    ),

    Uncommon(
            Rarity.UNCOMMON,
            0.75,
            BountifulStats.BOUNTIES_DONE_UNCOMMON,
            0.95,
            0.08
            //BountifulTriggers.COMPLETE_UNCOMMON
    ),

    Rare(
            Rarity.RARE,
            0.5,
            BountifulStats.BOUNTIES_DONE_RARE,
            0.9,
            0.16
            //BountifulTriggers.COMPLETE_RARE
    ),

    Epic(
            Rarity.EPIC,
            0.25,
            BountifulStats.BOUNTIES_DONE_EPIC,
            0.82,
            0.24
            //BountifulTriggers.COMPLETE_EPIC
    );

    fun trigger(playerEntity: ServerPlayerEntity) {
        //trigger.trigger(playerEntity.advancements)
    }


    companion object {

        fun tryTriggerAll(player: ServerPlayerEntity) {
            for (rarity in values()) {
                val stat = Stats.CUSTOM.get(rarity.stat)
                val progress = player.stats.getValue(stat)
                if (progress >= 3) {
                    rarity.trigger(player)
                }
            }
        }

        fun getRarityFromInt(n: Int): BountyRarity {
            return if (n in values().indices) {
                values()[n]
            } else {
                Common
            }
        }
    }
}

