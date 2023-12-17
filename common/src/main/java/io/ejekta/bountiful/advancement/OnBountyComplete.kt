package io.ejekta.bountiful.advancement

import com.google.gson.JsonObject
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.board.BoardBlockEntity
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class OnBountyComplete : AbstractCriterion<OnBountyComplete.Companion.Conditions>() {

    override fun conditionsFromJson(
        obj: JsonObject,
        predicate: Optional<LootContextPredicate>,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): Conditions {
        return Conditions(predicate)
    }

    fun trigger(player: ServerPlayerEntity, board: BoardBlockEntity, data: BountyData) {
        println("Triggering advancement OnComplete criterion")
        trigger(player) { true }
    }

    companion object {
        class Conditions(playerPredicate: Optional<LootContextPredicate>) : AbstractCriterionConditions(playerPredicate)
    }

}