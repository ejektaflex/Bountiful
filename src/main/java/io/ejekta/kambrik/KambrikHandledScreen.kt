package io.ejekta.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.toolkit.KGui
import io.ejekta.kambrik.gui.toolkit.KRect
import io.ejekta.kambrik.gui.toolkit.KWidget
import io.ejekta.kambrik.gui.toolkit.KGuiDsl
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

open class KambrikHandledScreen<SH : ScreenHandler>(
    handler: SH,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<SH>(handler, inventory, title) {

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

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onClick(mX - rect.x, mY - rect.y, button)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        cycleDrawnWidgets(mouseX, mouseY) { widget, rect, mX, mY ->
            widget.onMouseMoved(mX - rect.x, mY - rect.y)
        }
        super.mouseMoved(mouseX, mouseY)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KGuiDsl.() -> Unit) = KGui(
        this, { x to y }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        // Pass
    }

}