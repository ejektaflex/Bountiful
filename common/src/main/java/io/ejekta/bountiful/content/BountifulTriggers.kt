package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.advancement.SimpleCriterion
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.Criterion

object BountifulTriggers {

    init {
        println("Registering bountiful advancement criterion triggers")
    }

    val BOUNTY_COMPLETED = registerTrigger("bounty_completed", SimpleCriterion())
    val RUSH_ORDER = registerTrigger("rush_order", SimpleCriterion())
    val PROCRASTINATOR = registerTrigger("procrastinator", SimpleCriterion())
    val FETCH_QUEST = registerTrigger("fetch_quest", SimpleCriterion())

    private fun <T : Criterion<*>> registerTrigger(path: String, criterion: T): T {
        return criterion.apply {
            Criteria.VALUES[Bountiful.id(path)] = this
        }
    }
}