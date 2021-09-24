package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.toolkit.TKRect
import io.ejekta.kambrik.gui.toolkit.TKRoot
import io.ejekta.kambrik.gui.widgets.KListWidget
import io.ejekta.kambrik.gui.widgets.KScrollbarHorizontal
import io.ejekta.kambrik.gui.widgets.KScrollbarVertical
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.sin

class ToolkitScreen() : KambrikScreen(textLiteral("Picker")) {

    private val root = TKRoot()

    init {
        root.elements.add(
            TKRect(10, 10, 60, 30, 0xFF8888)
        )
    }

    private fun KGuiDsl.makeTree(treeW: Int) {
         this {
            area(treeW, height) {
                rect(0x00)
                textNoShadow(2, 2, textLiteral("Tree"))

                offset(0, 16) {
                    root.elements.forEachIndexed { index, element ->
                        textNoShadow(2, index * 13, textLiteral(element::class.simpleName ?: "???"))
                    }
                }

            }
        }
    }

    private fun KGuiDsl.makeInspector(inspectorW: Int) {
        this {
            area(width - inspectorW, 0, inspectorW, height) {
                rect(0x00)
                textNoShadow(2, 2, textLiteral("Inspector"))
            }
        }
    }

    private val backgroundGui = kambrikGui {

        val areaStart = 96

        makeInspector(96)
        makeTree(96)

        offset(areaStart, 0) {
            widget(root)
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