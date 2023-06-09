package io.ejekta.bountiful.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.kambrik.gui.KGui
import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KRect
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

abstract class KambrikScreen(title: Text) : Screen(title), KambrikScreenCommon {
    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseClicked(mouseX, mouseY, button)
        return super<Screen>.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseReleased(mouseX, mouseY, button)
        return super<Screen>.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikScreenCommon>.mouseMoved(mouseX, mouseY)
        super<Screen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        super<KambrikScreenCommon>.mouseScrolled(mouseX, mouseY, amount)
        return super<Screen>.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        onDrawBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        onDrawForeground(context, mouseX, mouseY, delta)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KGuiDsl.() -> Unit) = KGui(
        this, { 0 to 0 }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }

}