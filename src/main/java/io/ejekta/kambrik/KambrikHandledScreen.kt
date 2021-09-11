package io.ejekta.kambrik

import io.ejekta.kambrik.gui.toolkit.KGui
import io.ejekta.kambrik.gui.toolkit.KambrikGuiDsl
import io.ejekta.kambrik.gui.toolkit.kambrikGui
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

    fun kambrikGui(func: KambrikGuiDsl.() -> Unit) = KGui(
        this, { x to y }
    ) { apply(func) }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        // Pass
    }

}