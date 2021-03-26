package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


class EntityLogic(override val entry: BountyDataEntry) : IEntryLogic {

    override fun format(isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(player)
        val entity = Registry.ENTITY_TYPE.get(Identifier(entry.content))
        return when (isObj) {
            true -> entity.name.copy().formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(entity.name.colored(entry.rarity.color))
        }
    }

    override fun getProgress(player: PlayerEntity): Progress {
        return Progress(entry.extra, entry.amount)
    }

    override fun tryFinishObjective(player: PlayerEntity): Boolean {
        return false
    }

    override fun giveReward(player: PlayerEntity): Boolean {
        return false
    }

}