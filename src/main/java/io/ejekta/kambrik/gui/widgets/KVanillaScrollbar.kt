package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.ext.fapi.client
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KWidget
import net.minecraft.client.gui.screen.Screen
import kotlin.math.roundToInt

class KVanillaScrollbar(
    override val height: Int,
    private val knobSprite: KSpriteGrid.Sprite,
) : KWidget(knobSprite.width, height) {

    init {
        if (knobSprite.height > height) {
            throw Exception("Scrollbar knob height cannot be taller than the scrollbar itself! (${knobSprite.height} > $height)")
        }
    }

    private val moveRange = 0 .. height - knobSprite.height
    private var dragStart = 0
    private var isMoving = false

    /**
     * A number representing how far down the scrollbar has been scrolled
     */
    var percent: Double = 0.0
        private set

    /**
     * Returns a range of indices from a total list of numbers.
     * Used to figure out which slice of a list we should display
     * based on the scrollbar's current position
     */
    fun getIndices(total: Int, pick: Int): IntRange {
        return if (total <= pick) {
            0 until total
        } else {
            val latestStart = total - pick
            val start = (percent * latestStart).roundToInt()
            start until start + pick
        }
    }

    private fun knobPos(relY: Int): Int {
        return (relY - (knobSprite.height / 2)).coerceIn(moveRange)
    }

    override fun onClick(relX: Int, relY: Int, button: Int) {
        isMoving = true
    }

    override fun onRelease(relX: Int, relY: Int, button: Int) {
        isMoving = false
    }

    override fun onDraw(dsl: KGuiDsl) = dsl {
        val relY = dsl.mouseY - dsl.ctx.absY()
        val newPos = knobPos(relY)

        if (isMoving) {
            sprite(knobSprite, y = newPos)
            percent = newPos.toDouble() / moveRange.last
            dragStart = newPos
        } else {
            sprite(knobSprite, y = dragStart)
        }
    }
}