package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.gui.widgets.BountyLongButton
import io.ejekta.kambrik.KambrikHandledScreen
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.widgets.KVanillaScrollbar
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier


class BoardScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) :
    KambrikHandledScreen<ScreenHandler>(handler, inventory, title) {

    val boardHandler: BoardScreenHandler
        get() = handler as BoardScreenHandler

    init {
        sizeToSprite(BOARD_BG)
    }

    private val bgGui = kambrikGui {
        sprite(BOARD_BG)
    }

    private val buttons = (0 until 6).map {
        BountyLongButton(this, it)
    }

    private val slider = KVanillaScrollbar(120, SLIDER)

    val fgGui = kambrikGui {
        val levelData = BoardBlockEntity.levelProgress(boardHandler.totalDone)
        val percentDone = (levelData.second.toDouble() / levelData.third * 100).toInt()

        // Reputation Bar (background, foreground, label)
        offset(210, 56) {
            sprite(BAR_BG)
            sprite(BAR_FG, w = percentDone + 1)
            textCentered(-10, -2) {
                color(0xabff7a)
                +levelData.first.toString()
            }
        }

        // GUI Title
        textCentered(titleX - 53, titleY + 1) {
            color = 0xEADAB5
            +title
        }

        // Draw bounty buttons
        buttons.forEachIndexed { index, button ->
            widget(button, 5, 18 + index * 20)
        }

        // Scroll bar
        widget(slider, 166, 18)
        // Percent through scroll bar
        text(200, 18) {
            format(Formatting.GOLD)
            +"${slider.getIndices(10, 6)} - "
            +"%.2f".format(slider.percent).toDouble().toString()
        }
    }

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        bgGui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/new_board.png")
        private val WANDER = Identifier("textures/gui/container/villager2.png")

        private val BOARD_SHEET = KSpriteGrid(TEXTURE, texWidth = 512, texHeight = 256)
        private val BOARD_BG = BOARD_SHEET.Sprite(0f, 0f, 348, 146)

        private val WANDER_SHEET = KSpriteGrid(WANDER, texWidth = 512, texHeight = 256)
        private val BAR_BG = WANDER_SHEET.Sprite(0f, 186f, 102, 5)
        private val BAR_FG = WANDER_SHEET.Sprite(0f, 191f, 102, 5)
        private val SLIDER = WANDER_SHEET.Sprite(0f, 199f, 6, 26)
    }
}

