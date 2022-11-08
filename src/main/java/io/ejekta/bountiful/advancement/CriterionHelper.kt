package io.ejekta.bountiful.advancement

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.util.iterateBountyData
import kotlinx.serialization.json.JsonObject
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.JsonHelper
import java.util.function.Predicate

object CriterionHelper {

    private val predicateDeserializer = AdvancementEntityPredicateDeserializer(
        Bountiful.id("advancement_entity_predicate_deserializer"), null
    )

    fun <T : AbstractCriterionConditions> test(criterion: AbstractCriterion<T>, jsonCriterion: JsonObject, predicate: Predicate<T>): Boolean {
        if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
            val gsonData = JsonHelper.deserialize(jsonCriterion.toString()) // KSX Json to GSON Json
            val crits = AdvancementCriterion.fromJson(gsonData, predicateDeserializer)
            val absCond = crits.conditions as AbstractCriterionConditions

            //val abc = ShotCrossbowCriterion // AbstractCriterion

            // If the criterion we hooked into has the same ID as our Json criterion, then test
            if (criterion.id == absCond.id) {
                (absCond as? T)?.let { ourCond ->
                    // Will only run if our condition is the crossbow one in this example
                    return predicate.test(ourCond)
                }
            }

        }
        return false
    }

    fun <T: AbstractCriterionConditions> handle(player: ServerPlayerEntity, criterion: AbstractCriterion<T>, predicate: Predicate<T>) {

        1+1

        if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
            player.iterateBountyData {
                val triggerObjs = objectives.filter { it.criteria != null }.takeIf { it.isNotEmpty() }
                    ?: return@iterateBountyData false

                for (obj in triggerObjs) {
                    val trigger = obj.criteria!!
                    println("Handling bounty trigger objective")

                    val result = test(criterion, trigger.criterion, predicate)

                    println("RESULT: $result")

                    if (result) {
                        println("Do something I guess")
                    }
                }

                return@iterateBountyData false
            }
        }

//        if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
//            println("Player ${player.uuid} handling criterion ${criterion.id}, ${criterion::class.simpleName}")
//            val critLongString = """
//                {
//                  "conditions": {
//                    "items": [
//                      {
//                        "items": [
//                          "minecraft:lava_bucket"
//                        ]
//                      }
//                    ]
//                  },
//                  "trigger": "minecraft:inventory_changed"
//                }
//            """.trimIndent()
//
//            val jsonObj = Json.decodeFromString(JsonObject.serializer(), critLongString)
//
//            println(
//                "Pew: ${test(criterion, jsonObj, predicate)}"
//            )
//
//        }
    }

}