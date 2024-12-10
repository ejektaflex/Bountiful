package io.ejekta.bountiful.components

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.util.GameTime
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.MutableComponent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.max

@Serializable @JvmRecord
data class BountyInfo(
    val rarity: BountyRarity,
    val timeStarted: Long,
    val timeToComplete: Long,
    val timePickedUp: Long
) {

    fun timeLeftTicks(world: World): Long {
        return when (BountifulIO.configData.bounty.shouldHaveTimersAndExpire) {
            true -> max(timeStarted - world.time + (timeToComplete * GameTime.TICK_RATE), 0L)
            false -> 1L
        }
    }

    fun timeLeftSecs(world: World): Long {
        return timeLeftTicks(world) / GameTime.TICK_RATE
    }

    fun timeTakenTicks(world: World): Long {
        return world.time - timePickedUp
    }

    fun timeTakenSecs(world: World): Long {
        return timeTakenTicks(world) / GameTime.TICK_RATE
    }

    // ### Formatting ### //

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeftSecs(world))
    }

    companion object {
        val EMPTY = BountyInfo(BountyRarity.COMMON, -1L, -1L, -1L)
    }


}