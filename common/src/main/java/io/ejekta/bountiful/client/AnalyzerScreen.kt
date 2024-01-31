package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.client.widgets.BountyLongButton
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.widgets.KListWidget
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.gui.screen.KambrikHandledScreen
import io.ejekta.kambrik.gui.screen.KambrikScreen
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import java.awt.Color
import kotlin.math.ceil


class AnalyzerScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) : KambrikHandledScreen<ScreenHandler>(
    handler, inventory, title
) {

    init {
        backgroundWidth = 348
        backgroundHeight = 165
    }

    private val bgGui = kambrikGui {
        //img(TEXTURE, 349, 166)
    }

    // TODO make a class for this that caches and displays data for one pool only!

    val dec = BountifulContent.Decrees.find { it.id == "cleric" }!!

    val objPools = dec.objectivePools

    val ourPool = objPools.first().also { println("First pool is: ${it.id}") }

    val maxWorth = ourPool.items.maxOf { it.amount.max * it.unitWorth }

    val numPixels = 60

    // If the worth goes to 10,000 but pixels are 30, then each pixel is 333.33 in value
    val binWidth = maxWorth / numPixels

    val stepMap = mutableMapOf<Int, MutableSet<PoolEntry>>()

    init {
        for (pe in ourPool) {
            for (step in pe.worthSteps) {
                val binNum = (step / binWidth).toInt()
                val bin = stepMap.getOrPut(binNum) { mutableSetOf() }
                bin.add(pe)
            }
        }
    }

    val stepMax = stepMap.maxOf { it.value.size }

    val stepColors = stepMap.map {
        // 1.0 -> green (0.333), 0.0 -> red (0.0)
        val amt = it.value.size.toDouble() / stepMax
        it.key to Color.getHSBColor(amt.toFloat() * 0.333f, 1f, 1f).rgb
    }.toMap()

    private fun drawGui(): KGui {
        return kambrikGui {

            area(backgroundWidth, backgroundHeight) {
                rect(0x888888)
                text(0, 0) {
                    addLiteral("Hello There!")
                }

                // TODO also put one long hover segment on top that dynamically displays tooltip of the hovered segment
                // TODO this is much more performant than separate hover sections for each pixel

                offset(0, 20) {
                    for (i in 0 until numPixels) {
                        //rect(i * 2, 0, 2, 2, 0x88 * (stepMap[i] ?: emptySet()).size)
                        rect(i * 2, 0, 2, 2, stepColors[i] ?: 0x0)
                    }
                }
            }
        }
    }

    private val fgGui = drawGui()

    override fun onDrawBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // do nothing
    }

    override fun onDrawForeground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        bgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
    }

    companion object {
//        private val TEXTURE = Bountiful.id("board_bg")
//        private val SELECTOR = Bountiful.id("selector")
//        private val SCROLLER = Identifier("container/villager/scroller")
//        private val XP_FG = Identifier("container/villager/experience_bar_current")
//        private val XP_BG = Identifier("container/villager/experience_bar_background")
    }
}

