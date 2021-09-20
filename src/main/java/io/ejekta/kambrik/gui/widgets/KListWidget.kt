package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import kotlin.math.max

open class KListWidget<T>(
    private val items: () -> List<T>,
    private val itemWidth: Int,
    private val itemHeight: Int,
    private val rows: Int,
    private val scrollBar: KScrollbar,
    val onDrawItemFunc: KGuiDsl.(it: T) -> Unit = {}
) : KWidget() {

    // TODO make one selectable (perhaps a list of selectable, which could be empty?)

    override val height: Int
        get() = items().size * itemHeight

    override val width: Int
        get() = itemWidth

    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return dsl {
            val toIterate = items()
            scrollBar.getIndices(toIterate.size, rows).forEachIndexed { index, rowNumToShow ->
                offset(0, index * itemHeight) {
                    onDrawItem(this, toIterate[rowNumToShow])
                }
            }
        }
    }

    override fun onMouseScrolled(relX: Int, relY: Int, amount: Double) {
        scrollBar.scroll(-amount / max(1, items().size))
    }

    open fun onDrawItem(dsl: KGuiDsl, item: T) {
        dsl.onDrawItemFunc(item)
    }

}