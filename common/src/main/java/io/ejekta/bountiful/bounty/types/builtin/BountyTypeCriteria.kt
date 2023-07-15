package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.data.PoolEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier


class BountyTypeCriteria : IBountyObjective {

    override val id: Identifier = Identifier("criteria")


    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return true // TODO can we validate Criteria?
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        val progress = getProgress(entry, player)
        val textSum = if (entry.name != null) Text.literal(entry.name) else entry.translation
        return textSum.colored(progress.color).append(progress.neededText.colored(Formatting.WHITE))
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(
            if (entry.name != null) Text.literal(entry.name) else entry.translation
        )
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(entry.current, entry.amount)
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return entry.current >= entry.amount
    }

}