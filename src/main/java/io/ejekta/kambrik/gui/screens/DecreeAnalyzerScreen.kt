package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.widgets.KListWidget
import io.ejekta.kambrik.gui.widgets.KScrollbarVertical
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class DecreeAnalyzerScreen(val decree: Decree) : KambrikScreen(textLiteral("Picker")) {

    private val backgroundGui = kambrikGui {
        offset(width / 2 - BG.width / 2, height / 2 - BG.height / 2) { // centered
            sprite(BG)
            textCentered(BG.width / 2, 8) {
                +textLiteral("Decree Analyzer (${decree.id})")
            }
            decree.objectivePools.forEachIndexed { index, pool ->
                for (item in pool.content) {
                    item.worthSteps.forEach { step ->
                        val stepSlot = (step / 160).toInt()
                        rect(10 + stepSlot, 24 + (12 * index), 1, 10, 0xFF8888)
                    }
                }
            }
        }
    }

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        backgroundGui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {}

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/registry_picker.png")
        private val SHEET = KSpriteGrid(TEXTURE, 256, 256)
        private val WANDER = Identifier("textures/gui/container/villager2.png")
        private val BG = SHEET.Sprite(0f, 0f, 192, 120)
        private val WANDER_SHEET = KSpriteGrid(WANDER, texWidth = 512, texHeight = 256)
        private val SLIDER = WANDER_SHEET.Sprite(0f, 199f, 6, 26)
    }
}