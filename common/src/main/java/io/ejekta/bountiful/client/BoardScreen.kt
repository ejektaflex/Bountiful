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
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack


class BoardScreen(handler: AbstractContainerMenu, inventory: Inventory, title: Component) :
    KambrikContainerScreen<AbstractContainerMenu>(handler, inventory, title) {

    val boardHandler: BoardScreenHandler
        get() = menu as BoardScreenHandler

    private val guiWidth = 348
    private val guiHeight = 165

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

            // Selection highlight on selected stack
            if (!ItemStack.matches(boardHandler.container.selected(), ItemStack.EMPTY)) {
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
            }

            // Reputation Bar (background, foreground, label)
            offset(204, 75) {
                img(XP_BG, 102, 5)
                img(XP_FG, percentDone + 1, 5, x = 1)
                textCentered(-16, -2) {
                    color(0xabff7a)
                    addLiteral(levelData.first.toString()) {
                        format(BountyRarity.forReputation(levelData.first).color)
                    }
                }
                offset(-28, -2) {
                    if (isHovered(18, 8)) {
                        val repColor = BountyRarity.forReputation(levelData.first).color
                        tooltip {
                            addTranslate("bountiful.ui.reputation", "Reputation") {
                                color(0xabff7a)
                                addLiteral(" (${levelData.first}) ") {
                                    format(repColor)
                                }
                            }
                            addLiteral("(") {
                                color(0xabff7a)
                                addTranslate("bountiful.ui.discount", "Discount") {
                                    addLiteral(": ")
                                    addLiteral("%.1f".format((1 - BountyCreator.getDiscount(levelData.first)) * 100) + "%") {
                                        format(repColor)
                                    }
                                    addLiteral(")")
                                }
                            }
                        }
                    }
                }
            }

            // GUI Title
            textCentered(titleLabelX - 53, titleLabelY + 1) {
                color = 0xEADAB5
                add(title)
            }

            // Button list and scroll bar
            widget(buttonList, 5, 18)
            if (validButtons.isEmpty()) {
                textCentered(85, 78) {
                    color = 0xEADAB5
                    addTranslate("bountiful.ui.empty", "It's Empty! Check back soon!")
                }
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
    }

    override fun onDrawForeground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
        titleLabelX = (guiWidth - font.width(title)) / 2
    }

    companion object {
        private val TEXTURE = Bountiful.id("board_bg")
        private val SELECTOR = Bountiful.id("selector")
        private val SCROLLER = Identifier.parse("container/villager/scroller")
        private val XP_FG = Identifier.parse("container/villager/experience_bar_current")
        private val XP_BG = Identifier.parse("container/villager/experience_bar_background")
    }
}

