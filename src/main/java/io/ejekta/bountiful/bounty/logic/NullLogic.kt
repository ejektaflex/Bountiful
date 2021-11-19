package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object NullLogic : IEntryLogic {

    override val entry: BountyDataEntry
        get() = BountyDataEntry.DUMMY

    private fun logicUsageError(): Exception {
        return Exception("Cannot interact with a null logic object!")
    }

    override fun verifyValidity(player: PlayerEntity): MutableText {
        throw logicUsageError()
    }

    override fun textSummary(isObj: Boolean, player: PlayerEntity): Text {
        throw logicUsageError()
    }

    override fun textBoard(player: PlayerEntity): List<Text> {
        throw logicUsageError()
    }

    override fun getProgress(player: PlayerEntity): Progress {
        throw logicUsageError()
    }

    override fun tryFinishObjective(player: PlayerEntity): Boolean {
        throw logicUsageError()
    }

    override fun giveReward(player: PlayerEntity): Boolean {
        throw logicUsageError()
    }

}