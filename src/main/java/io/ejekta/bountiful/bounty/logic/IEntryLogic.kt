package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

interface IEntryLogic {

    val entry: BountyDataEntry

    fun format(isObj: Boolean, progress: Progress): Text

    fun getProgress(player: PlayerEntity): Progress

    fun finishObjective(player: PlayerEntity): Boolean

    fun giveReward(player: PlayerEntity): Boolean

    // ### Helpers ###

    val Pair<Int, Int>.isDone: Boolean
        get() = first == second

    val Pair<Int, Int>.color: Formatting
        get() = if (isDone) Formatting.GREEN else Formatting.RED

    fun Text.colored(progress: Pair<Int, Int>): MutableText {
        return copy().formatted(progress.color)
    }

    fun Text.colored(formatting: Formatting): MutableText {
        return copy().formatted(formatting)
    }

    val Pair<Int, Int>.needed
        get() = LiteralText(" ($first/$second)")

    val Pair<Int, Int>.giving
        get() = LiteralText("${second}x ")


}