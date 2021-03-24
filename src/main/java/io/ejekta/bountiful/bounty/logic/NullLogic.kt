package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

object NullLogic : IEntryLogic {

    private fun logicUsageError(): Exception {
        return Exception("Cannot interact with a null logic object!")
    }

    override fun format(entry: BountyDataEntry, isObj: Boolean, progress: Pair<Int, Int>): Text {
        throw logicUsageError()
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Pair<Int, Int> {
        throw logicUsageError()
    }

    override fun finishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        throw logicUsageError()
    }

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        throw logicUsageError()
    }
}