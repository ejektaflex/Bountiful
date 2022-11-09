package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.bounty.types.Progress
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text


class BountyTypeCriteria : IBountyObjective {

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
        return null
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        val progress = getProgress(entry, player)

        return Text.literal(entry.criteria?.description ?: "NO TRIGGER DESCRIPTION")
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        //return listOf(getEntityType(entry).name)
        return listOf(Text.literal(entry.criteria?.description ?: "BEEP BOOP"))
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(getNumTriggers(entry), entry.amount)
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return getNumTriggers(entry) >= entry.amount
    }

    private const val triggerCompleteAmount = "numTriggers"

//    fun incrementTriggers(playerEntity: ServerPlayerEntity, killedEntity: LivingEntity) {
//        playerEntity.iterateBountyData {
//            var didWork = false
//            val entityObjectives = objectives.filter { it.type == BountyType.TRIGGER }
//            if (entityObjectives.isEmpty()) return@iterateBountyData false
//            for (obj in entityObjectives) {
//                if (obj.content == killedEntity.type.identifier.toString()) {
//                    obj.tracking.putInt(
//                        triggerCompleteAmount,
//                        (obj.tracking.getInt(triggerCompleteAmount) + 1).coerceAtMost(obj.amount)
//                    )
//                    didWork = true
//                }
//            }
//            return@iterateBountyData didWork
//        }
//    }

}