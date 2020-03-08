package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.BountifulStats
import ejektaflex.bountiful.advancement.BountifulTrigger
import ejektaflex.bountiful.advancement.BountifulTriggers
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Rarity
import net.minecraft.stats.Stats
import net.minecraft.util.ResourceLocation

enum class BountyRarity(
        val itemRarity: Rarity,
        val exponent: Double,
        val stat: ResourceLocation//,
        //val trigger: BountifulTrigger
) {

    Common(
            Rarity.COMMON,
            1.0,
            BountifulStats.BOUNTIES_DONE_COMMON//,
            //BountifulTriggers.COMPLETE_COMMON
    ),

    Uncommon(
            Rarity.UNCOMMON,
            0.75,
            BountifulStats.BOUNTIES_DONE_UNCOMMON//,
            //BountifulTriggers.COMPLETE_UNCOMMON
    ),

    Rare(
            Rarity.RARE,
            0.5,
            BountifulStats.BOUNTIES_DONE_RARE//,
            //BountifulTriggers.COMPLETE_RARE
    ),

    Epic(
            Rarity.EPIC,
            0.25,
            BountifulStats.BOUNTIES_DONE_EPIC//,
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

