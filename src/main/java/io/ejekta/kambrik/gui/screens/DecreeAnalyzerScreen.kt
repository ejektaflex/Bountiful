package io.ejekta.kambrik.gui.screens

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.KambrikScreen
import io.ejekta.kambrik.gui.KSpriteGrid
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

class DecreeAnalyzerScreen(val decree: Decree) : KambrikScreen(textLiteral("Picker")) {

    //val someNeatList = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p")
    val someNeatList = listOf(1, 5, 10, 25, 50, 100, 250, 500, 1000)

    val scroller = KScrollbarVertical(62, SLIDER, 0x333333)

    val stepSize: Int
        get() = scroller.getItem(someNeatList) ?: 10

    val objScroller = KScrollbarVertical(33, SLIDER, 0x333333)

    val tabWidget = KListWidget(
        { decree.objectivePools },
        172, 11, 10,
        KListWidget.Orientation.VERTICAL,
        KListWidget.Mode.TOGGLE
    ) { listWidget, pool, selected ->
        val gutter = 80 // obj name side

        area(gutter, listWidget.itemHeight) {
            rect(0x181425)
            textNoShadow(0, 2, textLiteral(pool.id) { color( 0xe43b44 ) })
        }
        area(gutter, 0, listWidget.itemWidth - gutter, listWidget.itemHeight) {
            rect(0xFFFFFF)

            val bins = mutableMapOf<Int, MutableList<PoolEntry>>()

            for (item in pool.content) {

                val steps = item.worthSteps.map { (it / stepSize).toInt() }
                //println(steps)

                for (step in steps) {
                    bins.getOrPut(step) { mutableListOf() }.add(item)
                }
            }

            val maxBin = (bins.values.maxOfOrNull { it.size } ?: 1).coerceAtLeast(1)
            val maxPool = decree.objectivePools.maxOfOrNull { it.content.size } ?: 1

            //println(bins.keys)

            for ((step, entries) in bins) {
                area(step, 0, 1, 10) {
                    val shift = 1f - entries.size.toFloat().coerceAtLeast(1f) / maxPool
                    val colored = MathHelper.hsvToRgb((shift / 3f).coerceIn(0f..1f), 1f, 1f)
                    rect(colored)
                    onHover {
                        tooltip(listOf(textLiteral("Step: ${step * stepSize} - ${(step + 1) * stepSize}")) + entries.map {
                            textLiteral(it.content)
                        })
                    }
                }
            }

        }
    }.apply {
        attachScrollbar(objScroller)
    }

    private val backgroundGui = kambrikGui {
        spriteCentered(BG) {
            offset(6, 18) {
                widget(tabWidget)
                offset(172, 0) {
                    widget(objScroller)
                }
                offset(100, 35) {
                    widget(scroller)
                }
                text(120, 80, textLiteral("Scale: $stepSize"))

            }
            textCentered(BG.width / 2, 8) {
                addLiteral("Decree Analyzer (${decree.id})")
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