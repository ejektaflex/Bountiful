package io.ejekta.bountiful.common.util

import net.minecraft.text.LiteralText
import net.minecraft.text.Text

object GameTime {

    fun formatTimeExpirable(n: Long): Text {
        return if (n <= 0) {
            LiteralText("bountiful.tooltip.expired")
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