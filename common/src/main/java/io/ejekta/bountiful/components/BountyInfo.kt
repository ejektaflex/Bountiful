package io.ejekta.bountiful.components

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.GameTime
import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import kotlin.math.max

@Serializable @JvmRecord
data class BountyInfo(
    val rarity: BountyRarity,
    val timeStarted: Long,
    val timeToComplete: Long,
    val timePickedUp: Long
) {

    fun timeLeftTicks(level: Level): Long {
        return when (BountifulIO.configData.bounty.shouldHaveTimersAndExpire) {
            true -> max(timeStarted - level.gameTime + (timeToComplete * GameTime.TICK_RATE), 0L)
            false -> 1L
        }
    }

    fun timeLeftSecs(level: Level): Long {
        return timeLeftTicks(level) / GameTime.TICK_RATE
    }

    fun timeTakenTicks(level: Level): Long {
        return level.gameTime - timePickedUp
    }

    fun timeTakenSecs(level: Level): Long {
        return timeTakenTicks(level) / GameTime.TICK_RATE
    }

    // ### Formatting ### //

    fun formattedTimeLeft(level: Level): Component {
        return GameTime.formatTimeExpirable(timeLeftSecs(level))
    }

    companion object {
        val EMPTY = BountyInfo(BountyRarity.COMMON, -1L, -1L, -1L)
    }

}