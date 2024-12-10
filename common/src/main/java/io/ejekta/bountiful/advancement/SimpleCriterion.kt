package io.ejekta.bountiful.advancement

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.*

class SimpleCriterion : SimpleCriterionTrigger<SimpleCriterion.Companion.FreeCondition>() {

    override fun codec(): Codec<FreeCondition> = Codec.unit(FreeCondition())

    fun trigger(player: ServerPlayer) {
        trigger(player) { true }
    }

    companion object {
        class FreeCondition : SimpleInstance {
            override fun player(): Optional<ContextAwarePredicate> {
                return Optional.empty()
            }
        }

    }

}