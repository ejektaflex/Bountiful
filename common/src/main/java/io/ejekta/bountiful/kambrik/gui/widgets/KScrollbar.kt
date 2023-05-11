package io.ejekta.bountiful.kambrik.gui.widgets

import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KSpriteGrid
import io.ejekta.bountiful.kambrik.gui.KWidget
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
import kotlin.math.roundToInt

abstract class KScrollbar(
    protected val knobSprite: KSpriteGrid.Sprite,
    protected val bgColor: Int? = null
) : KWidget, KWidgetIndexSelector {

    abstract val scrollbarSize: Int
    abstract val knobSize: Int

    protected val moveRange
        get() = 0 .. scrollbarSize - knobSize
    protected var dragStart = 0

    val reactor = MouseReactor().apply {
        canDragStart = { true }
    }

    /**
     * A number representing how far down the scrollbar has been scrolled
     */
    val percent: Double
        get() = dragStart.toDouble() / moveRange.last

    fun <T> getItem(items: List<T>): T? {
        return getIndex(items)?.let { items.getOrNull(it) }
    }

    fun <T> getIndex(items: List<T>): Int? {
        return if (items.isEmpty()) null else getIndices(items.size, 1).first
    }

    /**
     * Returns a range of indices from a total list of numbers.
     * Used to figure out which slice of a list we should display
     * based on the scrollbar's current position
     */
    override fun getIndices(total: Int, pick: Int): IntRange {
        return if (total <= pick) {
            0 until total
        } else {
            val latestStart = total - pick
            val start = (percent * latestStart).roundToInt()
            start until start + pick
        }
    }

    fun scroll(pct: Double) {
        dragStart = (dragStart + ((moveRange.last - moveRange.first) * pct).toInt()).coerceIn(moveRange)
    }

    protected fun knobPos(relPos: Int): Int {
        return (relPos - (knobSize / 2)).coerceIn(moveRange)
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area.dsl {
            bgColor?.let {
                rect(width, height, color = it)
            }
        }
    }

}