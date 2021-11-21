package io.ejekta.kambrik.gui.reactor

open class MReactor(
    defaultCanPass: Boolean = false,
) : EventReactor(defaultCanPass) {

    var isHovered: Boolean = false
        private set

    open var dragPos = 0 to 0

    private var lastMouseDraggedPos = 0 to 0

    var isHeld = false
        private set

    var isPreSlop = false
        private set

    // Is true if the item is currently being dragged (past slop distance), false otherwise
    var isDragging: Boolean = false
        private set

    /**
     * Whether widgets behind this widget can be clicked on.
     */
    open fun canClickThrough(): Boolean {
        return false
    }

    open fun canDragStart(): Boolean {
        return true
    }

    open fun canDragStop(): Boolean {
        return true
    }

    /**
     * A callback that fires when we start dragging this widget.
     */
    open fun onDragStart(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires while the widget is being dragged.
     * Unlike onMouseMoved, this fires even when not hovering the widget.
     */
    open fun onDragging(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires when we stop dragging this widget.
     * Note: Drag end does not always occur inside the widget bounds!
     */
    open fun onDragEnd(relX: Int, relY: Int) {
        // No-op
    }

    /**
     * A callback that fires when the widget is clicked on.
     */
    open fun onClickDown(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    /**
     * A callback that fires when the mouse is released over the widget.
     */
    open fun onClickUp(relX: Int, relY: Int, button: Int) {
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

    open fun onMouseScrolled(relX: Int, relY: Int, amount: Double) {
        // No-op
    }

    fun doDragStart(relX: Int, relY: Int) {
        isDragging = true
        onDragStart(relX, relY)
    }

    fun doDragStop(relX: Int, relY: Int) {
        isDragging = false
        onDragEnd(relX, relY)
    }

    fun doClickDown(relX: Int, relY: Int, button: Int) {
        isHeld = true
        onClickDown(relX, relY, button)
    }

    fun doClickUp(relX: Int, relY: Int, button: Int) {
        isHeld = false
        onClickUp(relX, relY, button)
    }

}