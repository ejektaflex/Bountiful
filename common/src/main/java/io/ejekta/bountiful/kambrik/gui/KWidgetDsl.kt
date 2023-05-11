package io.ejekta.bountiful.kambrik.gui

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement

open class KWidgetDsl(
    var drawFunc: KGuiDsl.() -> Unit = {},
    open val width: Int,
    open val height: Int,
) : ParentElement {

    fun onDraw(func: KGuiDsl.() -> Unit) {
        drawFunc = func
    }

    val children = mutableListOf<Element>()

    override fun children(): MutableList<out Element> {
        return children
    }

    override fun isDragging(): Boolean {
        return false
    }

    override fun setDragging(dragging: Boolean) {
        //
    }

    override fun getFocused(): Element? {
        return null
    }

    override fun setFocused(focused: Element?) {
        //
    }

}