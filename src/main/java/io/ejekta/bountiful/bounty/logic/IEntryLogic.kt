package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

sealed interface IEntryLogic {

    fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): Text

    fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text>

    fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress

    fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean

    fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean

    fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText?

    fun setup(entry: BountyDataEntry, world: ServerWorld, pos: BlockPos) {

    }

    fun getDescription(entry: BountyDataEntry): Text {
        return entry.translation?.let {
            textTranslate(it)
        } ?: entry.name?.let {
            textLiteral(it)
        } ?: textLiteral(entry.content)
    }

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
        get() = Text.literal(" ($first/$second)")

    val Pair<Int, Int>.giving
        get() = Text.literal("${second}x ")

}