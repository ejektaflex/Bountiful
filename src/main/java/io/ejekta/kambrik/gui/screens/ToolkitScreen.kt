package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.toolkit.TKElement
import io.ejekta.kambrik.gui.toolkit.TKRect
import io.ejekta.kambrik.gui.toolkit.TKRoot
import io.ejekta.kambrik.gui.toolkit.`interface`.TreeElementDrawer
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

class ToolkitScreen : KambrikScreen(textLiteral("Picker")) {

    private val root = TKRoot()

    init {
        root.elements.add(
            TKRect(10, 10, 60, 30, 0xFF8888)
        )
    }

    val treeDrawer = TreeElementDrawer(root, w = 96)

    private fun KGuiDsl.makeTree(treeW: Int) {
         this {
            area(treeW, height) {
                rect(0x00)
                textNoShadow(2, 2, textLiteral("Tree"))

                offset(0, 16) {
                    root.elements.forEachIndexed { index, element ->
                        treeDrawer.element = element
                        offset(0, index * 13) {
                            widget(treeDrawer)
                        }
                    }
                }

            }
        }
    }

    private fun drawInspectorData(dsl: KGuiDsl, area: KGuiDsl.AreaDsl, sel: TKElement) {
        dsl {
            when (sel) {
                is TKRect -> {
                    textNoShadow(2, 2, textLiteral("Color"))
                    area(2, 11, area.w, 20) {
                        rect(sel.color, sel.alpha)
                    }
                    textNoShadow(2, 34, textLiteral("Position:"))
                    textNoShadow(2, 44, textLiteral("${sel.x}x${sel.y}"))
                    textNoShadow(2, 64, textLiteral("Size:"))
                    textNoShadow(2, 74, textLiteral("${sel.width}x${sel.height}"))

                }
            }
        }

    }

    private fun KGuiDsl.makeInspector(inspectorW: Int) {
        this {
            area(width - inspectorW, 0, inspectorW, height) {
                rect(0x00)
                textNoShadow(2, 2, textLiteral("Inspector"))
                // Content
                offset(0, 16) {
                    val sel = root.selected

                    sel?.let {
                        drawInspectorData(this, this@area, it)
                    }

                }

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