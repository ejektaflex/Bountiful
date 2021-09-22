package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.widgets.KListWidget
import io.ejekta.kambrik.gui.widgets.KScrollbarVertical
import io.ejekta.kambrik.gui.widgets.KSimpleWidget
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class RegistryPickerScreen<T>(val reg: Registry<T>) : KambrikScreen(textLiteral("Picker")) {

    private val scrollBar = KScrollbarVertical(84, SLIDER, 0xCCCCCC)

    private val listWidget = object : KListWidget<Identifier>(
        { reg.ids.toList() },
        174,
        12,
        7,
        onDrawItemFunc = { listWidget, item, selected ->
            rect(listWidget.itemWidth, listWidget.itemHeight, color = 0xFFFFFF) {
                textNoShadow(2, 2) {
                    +textLiteral(item.toString()) {
                        format(if (selected) Formatting.GOLD else Formatting.BLACK)
                    }
                }
            }
            onHoverArea(listWidget.itemWidth, listWidget.itemHeight) { // Highlight on hover
                rect(listWidget.itemWidth, listWidget.itemHeight, color = 0x00, alpha = 0x33)
            }
        }
    ) {
        init {
            attachScrollbar(scrollBar)
        }
    }

    private val backgroundGui = kambrikGui {
        offset(width / 2 - BG.width / 2, height / 2 - BG.height / 2) { // centered
            sprite(BG)
            textCentered(BG.width / 2, 8) {
                +textLiteral("Registry Picker")
            }
            widget(listWidget, 6, 20)
            widget(scrollBar, 180, 20)
            offset(6, 105) {
                rect(60, 11, color = 0xFF8888)
                text(2, 2, textLiteral("Print ${listWidget.selected.size} IDs") {
                    color = 0xFFFFFF
                })
            }
        }
    }

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        backgroundGui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {}

    companion object {
        private val SHEET = KSpriteGrid(Bountiful.id("textures/gui/container/registry_picker.png"), 256, 256)
        private val BG = SHEET.Sprite(0f, 0f, 192, 120)
        
        private val WANDER_SHEET = KSpriteGrid(Identifier("textures/gui/container/villager2.png"), texWidth = 512, texHeight = 256)
        private val SLIDER = WANDER_SHEET.Sprite(0f, 199f, 6, 26)
    }
}