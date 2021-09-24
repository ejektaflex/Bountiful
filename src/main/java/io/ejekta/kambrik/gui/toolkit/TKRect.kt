package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget

class TKRect(
    x: Int,
    y: Int,
    var w: Int,
    var h: Int,
    var color: Int = 0xFFFFFF,
    var alpha: Int = 0xFFFFFF
) : TKElement(x, y, w, h) {

    override val width: Int
        get() = w

    override val height: Int
        get() = h

    var dragPos = 0 to 0
    var sizePos = 0 to 0

    val resizeWidget = object : KWidget(5, 5) {
        override fun canDrag() = true
        override fun onDragStart(relX: Int, relY: Int) {
            sizePos = relX to relY
        }
        override fun onDragging(relX: Int, relY: Int) {
            val diff = relX - sizePos.first to relY - sizePos.second
            w += diff.first
            h += diff.second
        }

        override fun onDraw(dsl: KGuiDsl): KGuiDsl {
            return dsl {
                area(width, height) {
                    rect(0xFFFFFF)
                }
            }
        }
    }

    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return super.onDraw(dsl).apply {
            area(w, h) {
                rect(color, alpha)
            }
            offset(w, h) {
                widget(resizeWidget)
            }
        }
    }

    override fun canDrag() = true

    override fun onDragStart(relX: Int, relY: Int) {
        dragPos = relX to relY
    }

    override fun onDragging(relX: Int, relY: Int) {
        val diff = relX - dragPos.first to relY - dragPos.second
        x += diff.first
        y += diff.second
    }

}