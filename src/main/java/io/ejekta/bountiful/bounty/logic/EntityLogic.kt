package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


class EntityLogic(override val entry: BountyDataEntry) : IEntryLogic {

    val entityType: EntityType<*>
        get() = Registry.ENTITY_TYPE.get(Identifier(entry.content))

    override fun verifyValidity(player: PlayerEntity): MutableText? {
        val id = entityType.identifier
        if (id != Identifier(entry.content)) {
            return LiteralText("* '$id' is not a valid entity!")
        }
        return null
    }

    override fun format(isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(player)
        return when (isObj) {
            true -> entityType.name.copy().formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(entityType.name.colored(entry.rarity.color))
        }
    }

    override fun getProgress(player: PlayerEntity): Progress {
        return Progress(entry.extra, entry.amount)
    }

    override fun tryFinishObjective(player: PlayerEntity): Boolean {
        return entry.extra >= entry.amount
    }

    override fun giveReward(player: PlayerEntity): Boolean {
        return false
    }

}