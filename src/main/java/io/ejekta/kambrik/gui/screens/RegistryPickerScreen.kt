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

    private val okButton = KSimpleWidget(50, 11).create {
        onClickFunc = { relX, relY, button ->
            println("OK! I have these: ${listWidget.selected}")
            onClose()
        }
        onDrawFunc = {
            area(60, 11) {
                rect(color = if (isHovered) 0xFF4444 else 0xFF8888)
                textCentered(2, textLiteral("Submit"))
            }
        }
    }

    private val scrollBar = KScrollbarVertical(84, SLIDER, 0xCCCCCC)

    private val listWidget = object : KListWidget<Identifier>(
        { reg.ids.toList() },
        174,
        12,
        7,
        onDrawItemFunc = { listWidget, item, selected ->
            area(listWidget.itemWidth, listWidget.itemHeight) {
                rect(0xFFFFFF)
                textNoShadow(2, 2) {
                    +textLiteral(item.toString()) {
                        format(if (selected) Formatting.GOLD else Formatting.BLACK)
                    }
                }
                onHover {
                    rect(0x00, 0x33)
                }
            }
        }
    ) {
        init {
            attachScrollbar(scrollBar)
        }
    }

    private val backgroundGui = kambrikGui {
        spriteCentered(BG) {
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
                offset(120, 0) {
                    widget(okButton)
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
        private val SHEET = KSpriteGrid(Bountiful.id("textures/gui/container/registry_picker.png"), 256, 256)
        private val BG = SHEET.Sprite(0f, 0f, 192, 120)

        private val WANDER_SHEET = KSpriteGrid(Identifier("textures/gui/container/villager2.png"), texWidth = 512, texHeight = 256)
        private val SLIDER = WANDER_SHEET.Sprite(0f, 199f, 6, 26)
    }
}