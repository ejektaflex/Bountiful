package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.client.widgets.AnalyzerPoolWidget
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.gui.screen.KambrikContainerScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import kotlin.math.roundToInt


class AnalyzerScreen(handler: AbstractContainerMenu, inventory: Inventory, title: Component) : KambrikContainerScreen<AbstractContainerMenu>(
    handler, inventory, title
) {

    private var scanResolution = 1

    init {
        imageWidth = 177
        imageHeight = 167
    }

    private val bgGui = kambrikGui {
        img(TEXTURE, imageWidth, imageHeight)
    }

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


        val doot = (menu as? AnalyzerScreenHandler) ?: return

        val di = doot.container.getItem(0)[BountifulContent.DECREE_DATA] ?: return

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

            area(imageWidth, imageHeight) {
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

    override fun onDrawBackground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        // do nothing
    }

    override fun onDrawForeground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        bgGui.draw(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun init() {
        super.init()
    }

    companion object {
        private val TEXTURE = Bountiful.id("analyzer_bg")
        private val SCROLLER = Bountiful.id("analyzer_scroller")
//        private val SELECTOR = Bountiful.id("selector")
//        private val SCROLLER = ResourceLocation.parse("container/villager/scroller")
//        private val XP_FG = ResourceLocation.parse("container/villager/experience_bar_current")
//        private val XP_BG = ResourceLocation.parse("container/villager/experience_bar_background")
    }
}

