package io.ejekta.bountiful.advancement

import com.mojang.serialization.Codec
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class SimpleCriterion : AbstractCriterion<SimpleCriterion.Companion.FreeCondition>() {

    override fun getConditionsCodec(): Codec<FreeCondition> {
        return Codec.unit(FreeCondition())
    }

    fun trigger(player: ServerPlayerEntity) {
        trigger(player) { true }
    }

    companion object {
        class FreeCondition : Conditions {
            override fun player(): Optional<LootContextPredicate> {
                return Optional.empty()
            }
        }

    }

}