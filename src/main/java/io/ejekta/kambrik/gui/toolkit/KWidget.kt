package io.ejekta.kambrik.gui.toolkit

import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.util.math.MatrixStack

open class KWidget(
    open val width: Int,
    open val height: Int,
) : ParentElement, Drawable, Selectable {

    open fun onDraw(dsl: KambrikGuiDsl) {
        //
    }

    fun isHovered(dsl: KambrikGuiDsl): Boolean {
        return dsl.run {
            mouseX >= ctx.absX() && mouseX <= ctx.absX(width)
                    && mouseY >= ctx.absY() && mouseY <= ctx.absY(height)
        }
    }

    private val children = mutableListOf<KWidget>()

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

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // We defer drawing to the KGui calls
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
        // Implementable by subclasses
    }

    override fun getType(): Selectable.SelectionType {
        return Selectable.SelectionType.HOVERED
    }

}