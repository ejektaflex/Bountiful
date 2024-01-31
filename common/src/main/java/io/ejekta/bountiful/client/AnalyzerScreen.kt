package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.client.widgets.AnalyzerPoolWidget
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
        backgroundWidth = 172
        backgroundHeight = 160
    }

    private val bgGui = kambrikGui {
        //img(TEXTURE, 349, 166)
    }

    // TODO make a class for this that caches and displays data for one pool only!

    val dec = BountifulContent.Decrees.find { it.id == "butcher" }!!

    private val overallMaxWorth = dec.rewardPools.maxOf { pool ->
        pool.items.maxOf {
            println("${it.id}: ${it.amount.max * it.unitWorth}")
            it.amount.max * it.unitWorth
        }
    }

    val poolWidgets = dec.objectivePools.map {
        AnalyzerPoolWidget(it, overallMaxWorth)
    }

    private fun drawGui(): KGui {
        return kambrikGui {

            area(backgroundWidth, backgroundHeight) {
                rect(0x888888)
                text(0, 0) {
                    addLiteral("Hello There!")
                }

                offset(0, 20) {
                    for (i in poolWidgets.indices) {
                        val currWid = poolWidgets[i]
                        offset(0, i * currWid.height) {
                            widget(currWid)
                        }
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

