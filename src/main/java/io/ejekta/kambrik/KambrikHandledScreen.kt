package io.ejekta.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KGui
import io.ejekta.kambrik.gui.KRect
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.KGuiDsl
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

abstract class KambrikHandledScreen<SH : ScreenHandler>(
    handler: SH,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<SH>(handler, inventory, title) {

    abstract fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float)
    abstract fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float)

    val boundsStack = mutableListOf<Pair<KWidget, KRect>>()

    fun sizeToSprite(sprite: KSpriteGrid.Sprite) {
        backgroundWidth = sprite.width
        backgroundHeight = sprite.height
    }

    private fun cycleDrawnWidgets(func: (widget: KWidget, rect: KRect) -> Unit) {
        for (bounds in boundsStack) {
            func(bounds.first, bounds.second)
        }
    }

    private fun cycleDrawnWidgets(mouseX: Double, mouseY: Double, func: (widget: KWidget, rect: KRect, mX: Int, mY: Int) -> Unit) {
        for (bounds in boundsStack) {
            if (bounds.second.isInside(mouseX.toInt(), mouseY.toInt())) {
                func(bounds.first, bounds.second, mouseX.toInt(), mouseY.toInt())
            }
        }
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) { /* Pass here */ }
    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        onDrawBackground(matrices, mouseX, mouseY, delta)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        onDrawForeground(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            if (widget.canDrag() && !widget.isDragged) {
                widget.startDragging(mX - rect.x, mY - rect.y)
            }
            widget.onClick(mX - rect.x, mY - rect.y, button)
        }
        return super.mouseClicked(mouseX, mouseY, button)
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
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseMoved(mX - rect.x, mY - rect.y)
        }
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseScrolled(mX - rect.x, mY - rect.y, amount)
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KGuiDsl.() -> Unit) = KGui(
        this, { x to y }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }


}