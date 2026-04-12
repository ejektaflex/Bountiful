package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.client.widgets.BountyLongButton
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.widgets.KListWidget
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.gui.screen.KambrikContainerScreen
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack


class BoardScreen(handler: AbstractContainerMenu, inventory: Inventory, title: Component) :
    KambrikContainerScreen<AbstractContainerMenu>(handler, inventory, title, GUI_WIDTH, GUI_HEIGHT) {

    val boardHandler: BoardScreenHandler
        get() = menu as BoardScreenHandler

    private val guiWidth = GUI_WIDTH
    private val guiHeight = GUI_HEIGHT

    private val bgGui = kambrikGui {
        img(TEXTURE, guiWidth + 1, guiHeight + 1)
    }

    private val buttons = (0 until 21).map { BountyLongButton(this, it) }

    private val validButtons: List<BountyLongButton>
        get() = buttons.filter {
            (it.getStack()[BountifulContent.BOUNTY_OBJS] ?: emptyList()).isNotEmpty()
        }

    private val scroller = KScrollbarVertical(140, 6, 27, SCROLLER, 0x0)

    private val buttonList = KListWidget(
        { validButtons }, 160, 20, 7, KListWidget.Orientation.VERTICAL, KListWidget.Mode.SINGLE,
        { listWidget, item, selected ->
            widget(item)
        }
    ).apply {
        reactor.canPassThrough = { true }
        attachScrollbar(scroller)
    }

    fun drawGui(): KGui {
        return kambrikGui {
            val levelData = BoardBlockEntity.levelProgress(boardHandler.getTotalNumComplete())
            val percentDone = (levelData.second.toDouble() / levelData.third * 100).toInt()

            // Reputation Bar (background, foreground, label)
            offset(204, 75) {
                img(XP_BG, 102, 5)
                img(XP_FG, percentDone + 1, 5, x = 1)
                val repColor = BountyRarity.forReputation(levelData.first).color.color ?: 0xFFFFFF
                val repTextColor = 0xFF000000.toInt() or (repColor and 0xFFFFFF)
                textCenteredColored(-16, -2, textLiteral(levelData.first.toString()), repTextColor)
                offset(-28, -2) {
                    if (isHovered(18, 8)) {
                        val repFormat = BountyRarity.forReputation(levelData.first).color
                        tooltip {
                            addTranslate("bountiful.ui.reputation", "Reputation") {
                                color(0xabff7a)
                                addLiteral(" (${levelData.first}) ") {
                                    format(repFormat)
                                }
                            }
                            addLiteral("(") {
                                color(0xabff7a)
                                addTranslate("bountiful.ui.discount", "Discount") {
                                    addLiteral(": ")
                                    addLiteral("%.1f".format((1 - BountyCreator.getDiscount(levelData.first)) * 100) + "%") {
                                        format(repFormat)
                                    }
                                    addLiteral(")")
                                }
                            }
                        }
                    }
                }
            }

            // GUI Title
            textCenteredColored(titleLabelX - 53, titleLabelY + 1, title, 0xFFEADAB5.toInt())

            // Button list and scroll bar
            nextStratum()
            widget(buttonList, 5, 18)
            if (validButtons.isEmpty()) {
                textCenteredColored(
                    85,
                    78,
                    Component.translatable("bountiful.ui.empty", "It's Empty! Check back soon!"),
                    0xFFEADAB5.toInt()
                )
            } else {
                offset(166, 18) {
                    widget(scroller)
                    area(scroller.width, scroller.height) {
                        rect(0xb86f50, 0x48) // tint the scroller
                    }
                }
            }
        }
    }

    val fgGui = drawGui()

    override fun onDrawBackground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        bgGui.draw(context, mouseX, mouseY, delta)
        drawSelectionOverlay(context, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
        leftPos = (width - guiWidth) / 2
        topPos = (height - guiHeight) / 2
        titleLabelX = (guiWidth - font.width(title)) / 2
    }

    private fun drawSelectionOverlay(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        if (ItemStack.matches(boardHandler.container.selected(), ItemStack.EMPTY)) {
            return
        }
        kambrikGui {
            boardHandler.container.selectedIndex?.let {
                offset(179 + ((it % 7) * 18), 16 + ((it / 7) * 18)) {
                    img(SELECTOR, 20, 20)
                    offset(2, 2) {
                        area(16, 16) {
                            rect(0x0, 0x88)
                        }
                    }
                }
            }
        }.draw(context, mouseX, mouseY, delta)
    }

    companion object {
        private const val GUI_WIDTH = 348
        private const val GUI_HEIGHT = 165
        private val TEXTURE = Bountiful.id("board_bg")
        private val SELECTOR = Bountiful.id("selector")
        private val SCROLLER = Identifier.parse("container/villager/scroller")
        private val XP_FG = Identifier.parse("container/villager/experience_bar_current")
        private val XP_BG = Identifier.parse("container/villager/experience_bar_background")
    }
}

