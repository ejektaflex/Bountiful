package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.client.widgets.BountyLongButton
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.kambrik.KambrikHandledScreen
import io.ejekta.bountiful.kambrik.gui.KSpriteGrid
import io.ejekta.bountiful.kambrik.gui.widgets.KListWidget
import io.ejekta.bountiful.kambrik.gui.widgets.KScrollbarVertical
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class BoardScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) :
    KambrikHandledScreen<ScreenHandler>(handler, inventory, title) {

    val boardHandler: BoardScreenHandler
        get() = handler as BoardScreenHandler

    val toggledOut: Boolean = true

    val bgSprite: KSpriteGrid.Sprite
        get() = when(toggledOut) {
            true -> BOARD_BG_BIG
            false -> BOARD_BG_SMALL
        }

    val bgOffset: Int
        get() = when(toggledOut) {
            true -> 204
            false -> 4
        }


    init {
        sizeToSprite(bgSprite)
    }

    private val bgGui = kambrikGui {
        sprite(bgSprite)
    }

    private val buttons = (0 until 21).map { BountyLongButton(this, it) }

    private val validButtons: List<BountyLongButton>
        get() = buttons.filter { it.getBountyData().objectives.isNotEmpty() }

    private val scroller = KScrollbarVertical(140, SLIDER, 0x0)

    private val buttonList = KListWidget(
        { validButtons }, 160, 20, 7, KListWidget.Orientation.VERTICAL, KListWidget.Mode.SINGLE,
        { listWidget, item, selected ->
            widget(item)
        }
    ).apply {
        reactor.canPassThrough = { true }
        attachScrollbar(scroller)
    }

    val fgGui = kambrikGui {
        val levelData = BoardBlockEntity.levelProgress(boardHandler.getTotalNumComplete())
        val percentDone = (levelData.second.toDouble() / levelData.third * 100).toInt()

        // Selection highlight on selected stack
        if (toggledOut) {
            if (!ItemStack.areEqual(boardHandler.inventory.selected(), ItemStack.EMPTY)) {
                boardHandler.inventory.selectedIndex?.let {
                    offset(179 + ((it % 7) * 18), 16 + ((it / 7) * 18)) {
                        sprite(BOARD_HIGHLIGHT)
                        offset(2, 2) {
                            area(16, 16) {
                                rect(0x0, 0x88)
                            }
                        }
                    }
                }
            }
        }

        // Reputation Bar (background, foreground, label)
        offset(bgOffset, 75) {
            sprite(BAR_BG)
            sprite(BAR_FG, w = percentDone + 1)
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
                        addLiteral("Reputation ") {
                            color(0xabff7a)
                            addLiteral("(${levelData.first})") {
                                format(repColor)
                            }
                        }
                        addLiteral(" (Discount: ") {
                            color(0xabff7a)
                            addLiteral("%.1f".format((1 - BountyCreator.getDiscount(levelData.first)) * 100) + "%") {
                                format(repColor)
                            }
                            addLiteral(")")
                        }
                    }
                }
            }
        }

        // GUI Title
        textCentered(titleX - 53, titleY + 1) {
            color = 0xEADAB5
            add(title)
        }

        // Button list and scroll bar
        if (toggledOut) {
            widget(buttonList, 5, 18)
            if (validButtons.isEmpty()) {
                textCentered(85, 78) {
                    color = 0xEADAB5
                    addLiteral("It's Empty! Check back soon!")
                }
            } else {
                widget(scroller, 166, 18)
            }
        }

    }

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
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/new_new_board.png")
        private val WANDER = Identifier("textures/gui/container/villager2.png")

        private val BOARD_SHEET = KSpriteGrid(TEXTURE, texWidth = 512, texHeight = 512)
        private val BOARD_BG_BIG = BOARD_SHEET.Sprite(0f, 0f, 348, 165)
        private val BOARD_BG_SMALL = BOARD_SHEET.Sprite(0f, 166f, 176, 165)
        private val BOARD_HIGHLIGHT = BOARD_SHEET.Sprite(349f, 0f, 20, 20)

        private val WANDER_SHEET = KSpriteGrid(WANDER, texWidth = 512, texHeight = 256)
        private val BAR_BG = WANDER_SHEET.Sprite(0f, 186f, 102, 5)
        private val BAR_FG = WANDER_SHEET.Sprite(0f, 191f, 102, 5)
        private val SLIDER = WANDER_SHEET.Sprite(0f, 199f, 6, 27)
    }
}

