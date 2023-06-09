package io.ejekta.bountiful.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.kambrik.gui.KGui
import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KRect
import io.ejekta.bountiful.kambrik.gui.KSpriteGrid
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

abstract class KambrikHandledScreen<SH : ScreenHandler>(
    handler: SH,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<SH>(handler, inventory, title), KambrikScreenCommon {

    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    fun sizeToSprite(sprite: KSpriteGrid.Sprite) {
        backgroundWidth = sprite.width
        backgroundHeight = sprite.height
    }

    override fun drawForeground(context: DrawContext?, mouseX: Int, mouseY: Int) {  /* Pass here */ }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        onDrawBackground(context, mouseX, mouseY, delta)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        onDrawForeground(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseClicked(mouseX, mouseY, button)
        return super<HandledScreen>.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseReleased(mouseX, mouseY, button)
        return super<HandledScreen>.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikScreenCommon>.mouseMoved(mouseX, mouseY)
        super<HandledScreen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        super<KambrikScreenCommon>.mouseScrolled(mouseX, mouseY, amount)
        return super<HandledScreen>.mouseScrolled(mouseX, mouseY, amount)
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