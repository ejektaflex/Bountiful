package io.ejekta.bountiful.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object GameTime {

    fun formatTimeExpirable(secs: Long): Component {
        return if (secs <= 0) {
            Component.translatable("bountiful.tooltip.expired").withStyle(ChatFormatting.RED)
        } else {
            formatTickTime(secs)
        }
    }

    private fun formatTickTime(secs: Long): Component {
        val min = secs / 60
        val sec = secs % 60
        return if (min <= 0) {
            Component.literal("$sec").append(Component.translatable("bountiful.ui.shorthand.seconds"))
        } else {
            Component.literal("$min").append(
                Component.translatable("bountiful.ui.shorthand.minutes")
            ).append(Component.literal(" $sec"))
                .append(Component.translatable("bountiful.ui.shorthand.seconds"))
        }
    }

    const val TICK_RATE = 20

}