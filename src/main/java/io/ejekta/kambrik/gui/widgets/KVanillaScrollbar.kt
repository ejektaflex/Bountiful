package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.ext.fapi.client
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KWidget
import net.minecraft.client.gui.screen.Screen
import kotlin.math.roundToInt

abstract class KVanillaScrollbar(
    protected val knobSprite: KSpriteGrid.Sprite,
    protected val bgColor: Int? = null
) : KWidget() {

    abstract val scrollbarSize: Int
    abstract val knobSize: Int

    protected val moveRange
        get() = 0 .. scrollbarSize - knobSize
    protected var dragStart = 0
    protected var isMoving = false

    /**
     * A number representing how far down the scrollbar has been scrolled
     */
    var percent: Double = 0.0
        protected set

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

    protected fun knobPos(relPos: Int): Int {
        return (relPos - (knobSize / 2)).coerceIn(moveRange)
    }

    override fun onClick(relX: Int, relY: Int, button: Int) {
        isMoving = true
    }

    override fun onRelease(relX: Int, relY: Int, button: Int) {
        isMoving = false
    }

    override fun onDraw(dsl: KGuiDsl) = dsl {
        bgColor?.let {
            rect(0, 0, width, height, color = it)
        }
    }

}