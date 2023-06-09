package io.ejekta.bountiful.kambrik

import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KRect
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack

interface KambrikScreenCommon : Element {
    val boundsStack: MutableList<Pair<MouseReactor, KRect>>
    val areaClickStack: MutableList<Pair<() -> Unit, KRect>>
    val modalStack: MutableList<KGuiDsl.() -> Unit>
    fun onDrawBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)
    fun onDrawForeground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)

    fun cycleDrawnWidgets(func: (widget: MouseReactor, rect: KRect) -> Unit) {
        for (bounds in boundsStack) {
            func(bounds.first, bounds.second)
        }
    }

    fun cycleDrawnWidgetsInBounds(mouseX: Double, mouseY: Double, func: (widget: MouseReactor, rect: KRect, mX: Int, mY: Int) -> Unit) {
        for (bounds in boundsStack) {
            if (bounds.second.isInside(mouseX.toInt(), mouseY.toInt())) {
                func(bounds.first, bounds.second, mouseX.toInt(), mouseY.toInt())
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (bounds in boundsStack) {
            val widget = bounds.first
            val rect = bounds.second
            if (bounds.second.isInside(mouseX.toInt(), mouseY.toInt())) {

                if (widget.canDragStart() && !widget.isDragging) {
                    widget.doDragStart(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y)
                }

                widget.doClickDown(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y, button)

                if (!widget.canPassThrough()) {
                    break // If we cannot continue down the bounds stack because there's no clickthrough, return
                }
            }
        }
        for (clicks in areaClickStack) {
            if (clicks.second.isInside(mouseX.toInt(), mouseY.toInt())) {
                clicks.first()
            }
        }
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        cycleDrawnWidgets { widget, rect ->
            if (widget.canDragStop() && widget.isDragging) {
                widget.doDragStop(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y)
            }
        }
        cycleDrawnWidgetsInBounds(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.doClickUp(mX - rect.x, mY - rect.y, button)
        }
        return true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        cycleDrawnWidgetsInBounds(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseMoved(mX - rect.x, mY - rect.y)
        }
        cycleDrawnWidgets { widget, rect ->
            if (widget.isDragging) {
                widget.onDragging(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y)
            }
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        cycleDrawnWidgetsInBounds(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseScrolled(mX - rect.x, mY - rect.y, amount)
        }
        return true
    }

}