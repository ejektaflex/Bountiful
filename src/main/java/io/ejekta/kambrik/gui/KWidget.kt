package io.ejekta.kambrik.gui

open class KWidget(
    open val width: Int = 0,
    open val height: Int = 0,
) {

    var isDragged = false
        private set

    /**
     * Whether this widget is allowed to receive drag events
     */
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

    /**
     * A callback that fires when we start dragging this widget.
     */
    open fun onDragStart(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires when we stop dragging this widget.
     * Note: Drag end does not always occur inside of the widget bounds!
     */
    open fun onDragEnd(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires when the widget is clicked on.
     */
    open fun onClick(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    /**
     * A callback that fires when the mouse is released over the widget.
     */
    open fun onRelease(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    /**
     * A callback that fires when the mouse is hovering over the widget.
     * Note: To draw while hovering, use `isHovered` inside the onDraw GUI DSL instead.
     */
    open fun onHover(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires when the mouse moves while hovering the widget.
     */
    open fun onMouseMoved(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that allows the widget to draw to the screen.
     */
    open fun onDraw(dsl: KGuiDsl): KGuiDsl {
        /* No-op
        return dsl {
            ...
        }
         */
        return dsl
    }

}