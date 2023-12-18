package io.ejekta.bountiful.util

import net.minecraft.text.Text
import net.minecraft.util.Formatting

object GameTime {

    fun formatTimeExpirable(n: Long): Text {
        return if (n <= 0) {
            Text.translatable("bountiful.tooltip.expired").formatted(Formatting.RED)
        } else {
            formatTickTime(n, 20)
        }
    }

    private fun formatTickTime(n: Long, tps: Long): Text {
        val tot = n / tps
        val min = tot / 60
        val sec = tot % 60
        return if (min <= 0) {
            Text.literal("$sec").append(Text.translatable("bountiful.ui.shorthand.seconds"))
        } else {
            Text.literal("$min").append(
                Text.translatable("bountiful.ui.shorthand.minutes")
            ).append(Text.literal("$sec"))
                .append(Text.translatable("bountiful.ui.shorthand.seconds"))
        }
    }

}