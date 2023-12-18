package io.ejekta.bountiful.advancement

import com.google.gson.JsonObject
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

open class SimpleCriterion : AbstractCriterion<SimpleCriterion.Companion.FreeCondition>() {

    override fun conditionsFromJson(
        obj: JsonObject,
        predicate: Optional<LootContextPredicate>,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): FreeCondition {
        return FreeCondition(predicate)
    }

    fun trigger(player: ServerPlayerEntity) {
        trigger(player) { true }
    }

    companion object {
        class FreeCondition(playerPredicate: Optional<LootContextPredicate>) : AbstractCriterionConditions(playerPredicate)

    }

}