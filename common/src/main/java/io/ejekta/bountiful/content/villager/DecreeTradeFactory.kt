package io.ejekta.bountiful.content.villager

import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.random.Random
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradeOffers
import kotlin.math.pow
import kotlin.random.nextInt
import kotlin.random.Random as KotlinRandom

class DecreeTradeFactory : TradeOffers.Factory {
    override fun create(entity: Entity?, random: Random): TradeOffer {
        val tradeValues = KotlinRandom.nextInt(2..5)
        val di = DecreeItem.create(DecreeSpawnCondition.WANDERING_TRADER, ranked = tradeValues, DecreeSpawnRank.RANDOM)
        val finalRank = DecreeData[di].ids.size
        return TradeOffer(
            // 2^(finalRank-1) + 1 = 2, 3, 5, 9
            ItemStack(Items.EMERALD, 2.0.pow(finalRank - 1).toInt() + 1),
            di,
            (tradeValues / 2), // 1-2 To Trade
            1,
            0.1f
        )
    }
}