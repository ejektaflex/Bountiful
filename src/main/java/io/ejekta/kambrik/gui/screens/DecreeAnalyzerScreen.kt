package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.widgets.KListWidget
import io.ejekta.kambrik.gui.widgets.KScrollbarHorizontal
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class DecreeAnalyzerScreen(val decree: Decree) : KambrikScreen(textLiteral("Picker")) {

    val someNeatList = listOf("a", "b", "c", "d", "e", "f", "g", "h")

    val scroller = KScrollbarHorizontal(100, SLIDER, 0x333333)

    val tabWidget = object : KListWidget<String>({ someNeatList }, 10, 11, 4,
        Orientation.HORIZONTAL, { listWidget, item, selected ->
            area(listWidget.itemWidth, listWidget.itemHeight) {
                rect(if (selected) 0x0 else 0x88FF88)
                textNoShadow(2, 2, textLiteral(item) {
                    format(if (selected) Formatting.RED else Formatting.BLACK)
                })
                onHover {
                    rect(0x00, 0x33)
                }
            }
        }
    ) {
        override fun canMultiSelect() = false
    }.apply {
        attachScrollbar(scroller)
    }

    private val backgroundGui = kambrikGui {
        spriteCentered(BG) {
            offset(10, 75) {
                widget(tabWidget)
                offset(0, 11) {
                    widget(scroller)
                }
            }
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