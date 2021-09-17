package io.ejekta.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.toolkit.KGui
import io.ejekta.kambrik.gui.toolkit.KWidget
import io.ejekta.kambrik.gui.toolkit.KambrikGuiDsl
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

    fun addWidget(widget: KWidget) {
        addDrawableChild(widget)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KambrikGuiDsl.() -> Unit) = KGui(
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