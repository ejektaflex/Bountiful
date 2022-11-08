package io.ejekta.bountiful.advancement

import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.server.network.ServerPlayerEntity

object AdvancementHelper {

    fun <T: AbstractCriterionConditions> handle(player: ServerPlayerEntity, crit: AbstractCriterion<T>) {
        if (crit !is TickCriterion && crit !is EnterBlockCriterion) {
            println("Player ${player.uuid} handling criterion ${crit.id}, ${crit::class.simpleName}")
            val critLongString = """
                
            """.trimIndent()


        }
    }

}