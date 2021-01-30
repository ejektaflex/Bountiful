package io.ejekta.bountiful.common.util

import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting

object GameTime {

    fun formatTimeExpirable(n: Long): Text {
        return if (n <= 0) {
            TranslatableText("bountiful.tooltip.expired").formatted(Formatting.RED)
        } else {
            formatTickTime(n)
        }
    }

    private fun formatTickTime(n: Long): Text {
        return LiteralText(
            if (n / 60 <= 0) {
                "${n}s"
            } else {
                "${n / 60}m ${n % 60}s"
            }
        )
    }

}