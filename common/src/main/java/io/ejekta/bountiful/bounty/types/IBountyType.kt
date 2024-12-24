package io.ejekta.bountiful.bounty.types

import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player

sealed interface IBountyType {

    val id: ResourceLocation

    fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): MutableComponent

    fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component>

    fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean

    fun getDescription(entry: BountyDataEntry): MutableComponent {
        return entry.name?.let {
            Component.literal(it)
        } ?: Component.translatable(entry.id)
    }

    // ### Helpers ###

    val Pair<Int, Int>.isDone: Boolean
        get() = first == second

    val Pair<Int, Int>.color: ChatFormatting
        get() = if (isDone) ChatFormatting.GREEN else ChatFormatting.RED

    fun Component.colored(progress: Pair<Int, Int>): MutableComponent {
        return copy().colored(progress.color)
    }

    fun Component.colored(formatting: ChatFormatting): MutableComponent {
        return copy().withStyle(formatting)
    }

    val Pair<Int, Int>.needed
        get() = Component.literal(" ($first/$second)")

    val Pair<Int, Int>.giving
        get() = Component.literal("${second}x ")

}