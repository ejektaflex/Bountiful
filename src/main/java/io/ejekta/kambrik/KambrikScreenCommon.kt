package io.ejekta.kambrik

import io.ejekta.kambrik.gui.KRect
import io.ejekta.kambrik.gui.KWidget
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack

interface KambrikScreenCommon : Element {
    val boundsStack: MutableList<Pair<KWidget, KRect>>
    val areaClickStack: MutableList<Pair<() -> Unit, KRect>>
    fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float)
    fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float)

    fun cycleDrawnWidgets(func: (widget: KWidget, rect: KRect) -> Unit) {
        for (bounds in boundsStack) {
            func(bounds.first, bounds.second)
        }
    }

    fun cycleDrawnWidgets(mouseX: Double, mouseY: Double, func: (widget: KWidget, rect: KRect, mX: Int, mY: Int) -> Unit) {
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
                if (widget.canDrag() && !widget.isDragged) {
                    widget.startDragging(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y)
                }

                widget.onClick(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y, button)

                if (!widget.canClickThrough()) {
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
            if (widget.canDrag() && widget.isDragged) {
                widget.stopDragging(mouseX.toInt() - rect.x, mouseY.toInt() - rect.y)
            }
        }
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onRelease(mX - rect.x, mY - rect.y, button)
        }
        return true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseMoved(mX - rect.x, mY - rect.y)
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseScrolled(mX - rect.x, mY - rect.y, amount)
        }
        return true
    }

}