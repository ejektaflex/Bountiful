package io.ejekta.kambrik.gui

open class KWidget(
    open val width: Int = 0,
    open val height: Int = 0,
) {

    var isDragged = false
        private set

    open fun canDrag(): Boolean {
        return false
    }

    fun startDragging(relX: Int, relY: Int) {
        isDragged = true
        onDragStart(relX, relY)
    }

    fun stopDragging(relX: Int, relY: Int) {
        isDragged = false
        onDragEnd(relX, relY)
    }

    open fun onDragStart(relX: Int, relY: Int) {
        // No-op
    }

    open fun onDragEnd(relX: Int, relY: Int) {
        // No-op
    }

    open fun canSelect(): Boolean {
        return false
    }

    open fun onClick(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    open fun onRelease(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    open fun onHover(relX: Int, relY: Int) {
        // No-op
    }

    open fun onMouseMoved(relX: Int, relY: Int) {
        // No-op
    }

    open fun onDraw(dsl: KGuiDsl): KGuiDsl {
        /* No-op
        return dsl {
            ...
        }
         */
        return dsl
    }

}