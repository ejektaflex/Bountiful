package io.ejekta.bountiful.advancement

import io.ejekta.bountiful.Bountiful
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criterion.ConditionsContainer
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.ShotCrossbowCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.JsonHelper
import java.util.function.Predicate
import kotlin.reflect.typeOf

object AdvancementHelper {

    fun <T: AbstractCriterionConditions> handle(player: ServerPlayerEntity, criterion: AbstractCriterion<T>, predicate: Predicate<T>) {
        if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
            println("Player ${player.uuid} handling criterion ${criterion.id}, ${criterion::class.simpleName}")
            val critLongString = """
                {
                  "conditions": {
                    "item": {
                      "items": [
                        "minecraft:crossbow"
                      ]
                    }
                  },
                  "trigger": "minecraft:shot_crossbow"
                }
            """.trimIndent()

            val blah = JsonHelper.deserialize(critLongString)

            println("Blah: $blah")

            val crits = AdvancementCriterion.fromJson(blah, AdvancementEntityPredicateDeserializer(
                Bountiful.id("advancement_entity_predicate_deserializer"), null
            ))


            println("Crits: $crits")

//            val playerAdvancementTracker = player.advancementTracker
//            val set: Set<ConditionsContainer<T>> = criterion.progressions.get(playerAdvancementTracker)

            val absCond = crits.conditions as AbstractCriterionConditions



            println("AbsCond: $absCond")

            //val abc = ShotCrossbowCriterion // AbstractCriterion

            if (criterion.id == absCond.id) {
                (absCond as? T)?.let { ourCond ->
                    println("Testing crossbow..")
                    // Will only run if our condition is the crossbow one in this example
                    val passed = predicate.test(ourCond)
                    println("Passed: $passed")
                }
            }

        }
    }

    inline fun <reified A> dootCheck(advCrit: AdvancementCriterion): Boolean {
        return advCrit is A
    }

}