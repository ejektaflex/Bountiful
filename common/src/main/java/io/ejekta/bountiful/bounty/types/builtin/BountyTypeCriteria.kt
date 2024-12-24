package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player


class BountyTypeCriteria : IBountyObjective {

    override val id: ResourceLocation = ResourceLocation.parse("criteria")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return true // TODO can we validate Criteria?
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): MutableComponent {
        val progress = getProgress(entry, player, current)
        val textSum = if (entry.name != null) Component.literal(entry.name) else entry.translation
        return textSum.withColor(progress.color.id).append(progress.neededText.withColor(ChatFormatting.WHITE.id))
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return listOf(
            if (entry.name != null) Component.literal(entry.name) else entry.translation
        )
    }

    override fun getProgress(entry: BountyDataEntry, player: Player, current: Int): Progress {
        return Progress(current, entry.amount)
    }

    override fun consumeObjectives(entry: BountyDataEntry, player: Player, current: Int): Boolean {
        return current >= entry.amount
    }

}