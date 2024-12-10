package io.ejekta.bountiful.util

import net.minecraft.text.Text
import net.minecraft.util.Formatting

object GameTime {

    fun formatTimeExpirable(secs: Long): Text {
        return if (secs <= 0) {
            Text.translatable("bountiful.tooltip.expired").formatted(ChatFormatting.RED)
        } else {
            formatTickTime(secs)
        }
    }

    private fun formatTickTime(secs: Long): Text {
        val min = secs / 60
        val sec = secs % 60
        return if (min <= 0) {
            Component.literal("$sec").append(Text.translatable("bountiful.ui.shorthand.seconds"))
        } else {
            Component.literal("$min").append(
                Text.translatable("bountiful.ui.shorthand.minutes")
            ).append(Component.literal(" $sec"))
                .append(Text.translatable("bountiful.ui.shorthand.seconds"))
        }
    }

    const val TICK_RATE = 20

}