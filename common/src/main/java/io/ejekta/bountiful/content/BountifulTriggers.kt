package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.advancement.OnBountyComplete
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.util.Identifier

object BountifulTriggers {

    init {
        println("Registering bountiful advancement criterion triggers")
    }

    val BOUNTY_COMPLETED = registerTrigger(Bountiful.id("bounty_completed"), OnBountyComplete())

    private fun <T : Criterion<*>> registerTrigger(id: Identifier, criterion: T): T {
        return criterion.apply {
            Criteria.VALUES[id] = this
        }
    }
}