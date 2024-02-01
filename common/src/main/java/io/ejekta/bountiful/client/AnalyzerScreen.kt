package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.client.widgets.AnalyzerPoolWidget
import io.ejekta.bountiful.client.widgets.BountyLongButton
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
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
import kotlin.math.roundToInt


class AnalyzerScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) : KambrikHandledScreen<ScreenHandler>(
    handler, inventory, title
) {

    private var scanResolution = 1

    init {
        backgroundWidth = 177
        backgroundHeight = 167
    }

    private val bgGui = kambrikGui {
        img(TEXTURE, backgroundWidth, backgroundHeight)
    }

    // TODO make a class for this that caches and displays data for one pool only!

    val dec = BountifulContent.Decrees.find { it.id == "fletcher" }!!

    private val overallMaxWorth = dec.rewardPools.maxOf { pool ->
        pool.items.maxOf {
            println("${it.id}: ${it.amount.max * it.unitWorth}")
            it.amount.max * it.unitWorth
        }
    }

    private var poolWidgets = listOf<AnalyzerPoolWidget>()

    enum class Mode(val symbol: String) {
        OBJ("O"),
        REW("R")
    }

    var showMode = Mode.OBJ

    val modeClicker = MouseReactor().apply {
        onClickDown = { relX: Int, relY: Int, button: Int ->
            showMode = Mode.entries[(showMode.ordinal + 1) % Mode.entries.size]
            refreshWidgets()
        }
    }

    fun refreshWidgets() {

        println("Refreshing widgets")

        val doot = (screenHandler as? AnalyzerScreenHandler) ?: return

        val di = DecreeData[doot.inventory.getStack(0)]

        val decrees = di.ids.mapNotNull { BountifulContent.Decrees.find { d -> d.id == it } }

        val allPools = decrees.map {
            if (showMode == Mode.OBJ) it.objectivePools else it.rewardPools
        }.flatten().toSet().toList()

        poolWidgets = allPools.map {
            //val heightBuff = if (it == allPools.last()) (64 - 64/allPools.size) else 0
            AnalyzerPoolWidget(it, overallMaxWorth, scanResolution, 64 / allPools.size + 0)
        }.sortedBy {
            it.pool.id
        }

    }

    // 18x43
    private val scroller = KScrollbarVertical(43, 18, 8, SCROLLER, 0x0)

    private fun drawGui(): KGui {
        return kambrikGui {

            area(backgroundWidth, backgroundHeight) {
                text(7, 6) {
                    addLiteral("Decree Analyzer")
                }

                area(152, 6, 18, 9) {
                    rect(0x0)
                    reactWith(modeClicker)
                    offset(9, 1) {
                        textCentered {
                            addLiteral(showMode.symbol)
                        }
                    }
                    onHover {
                        tooltip {
                            addLiteral("Click to change mode (Current: ${showMode})")
                        }
                    }
                }

                offset(9, 17) {
                    for (i in poolWidgets.indices) {
                        val currWid = poolWidgets[i]
                        offset(0, i * currWid.height) {
                            widget(currWid)
                        }
                    }
                }

                offset(152, 38) {
                    area(18, 43) {
                        //reactWith(mouseReact)
                        widget(scroller)
                        if (!scroller.reactor.isDragging) {
                            onHover {
                                tooltip { addLiteral("Sample Resolution") }
                            }
                        } else {
                            val resolutions = listOf(1, 2, 3, 4, 5, 6, 8, 10, 12, 15)
                            val resIndex = (scroller.percent * (resolutions.size - 1)).roundToInt() + 1
                            if (scanResolution != resIndex) {
                                scanResolution = resIndex
                                refreshWidgets()
                            }
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
        private val TEXTURE = Bountiful.id("analyzer_bg")
        private val SCROLLER = Bountiful.id("analyzer_scroller")
//        private val SELECTOR = Bountiful.id("selector")
//        private val SCROLLER = Identifier("container/villager/scroller")
//        private val XP_FG = Identifier("container/villager/experience_bar_current")
//        private val XP_BG = Identifier("container/villager/experience_bar_background")
    }
}

