package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text


object CriteriaLogic : IEntryLogic {

    fun getNumTriggers(entry: BountyDataEntry): Int {
        return entry.tracking.getInt(triggerCompleteAmount)
    }

    fun setNumTriggers(entry: BountyDataEntry, value: Int) {
        entry.tracking.putInt(triggerCompleteAmount, value)
    }

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
        return null
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(entry, player)
//        return when (isObj) {
//            true -> Text.literal("Kill ").append(
//                getEntityType(entry).name.copy()
//            ).formatted(progress.color).append(
//                progress.neededText.colored(Formatting.WHITE)
//            )
//            false -> error("Cannot have an entity (${entry.content}) as a reward.")
//        }
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

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return false
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