package io.ejekta.bountiful.content.villager

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import java.util.*
import kotlin.math.pow
import kotlin.random.nextInt
import kotlin.random.Random as KotlinRandom

class DecreeTradeFactory : VillagerTrades.ItemListing {
    override fun getOffer(entity: Entity, random: RandomSource): MerchantOffer? {
        val tradeValues = KotlinRandom.nextInt(2..5)
        val di = DecreeItem.create(DecreeSpawnCondition.WANDERING_TRADER, ranked = tradeValues, DecreeSpawnRank.RANDOM)
        val finalRank = di[BountifulContent.DECREE_DATA]?.ids?.size ?: 0
        return MerchantOffer(
            // 2^(finalRank-1) + 1 = 2, 3, 5, 9
            ItemCost(Items.EMERALD, 2.0.pow(finalRank - 1).toInt() + 1),
            di,
            (tradeValues / 2), // 1-2 To Trade
            1,
            0.1f
        )
    }
}