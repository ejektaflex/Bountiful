package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemLogic
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.gui.widgets.BountyLongButton
import io.ejekta.kambrik.KambrikHandledScreen
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.toolkit.widgets.KVanillaButton
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class BoardScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) :
    KambrikHandledScreen<ScreenHandler>(handler, inventory, title) {

    val boardHandler: BoardScreenHandler
        get() = handler as BoardScreenHandler

    init {
        sizeToSprite(BOARD_BG)
    }

    val bgGui = kambrikGui {
        sprite(BOARD_BG)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        bgGui.draw(matrices, mouseX, mouseY, delta)
    }

    //val button = KVanillaButton(395)

    val buttons = (0 until 6).map {
        BountyLongButton(this, it)
    }

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

            // Test colored rect
            rect(30, 30, 50, 50, 0xabff7a) {
                textCentered(25, 0) {
                    color(0xFF0000)
                    +levelData.first.toString()
                }
            }

        }

        // GUI Title
        textCentered(titleX - 53, titleY + 1) {
            color = 0xEADAB5
            +title
        }

        // Draw bounty objective buttons
        buttons.forEachIndexed { index, button ->
            widget(button, 5, 18 + index * 20)
        }

    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        fgGui.draw(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) { /* Pass here */ }




    inner class WidgetButtonBounty(var dataIndex: Int, inX: Int, inY: Int, press: PressAction) : ButtonWidget(
        inX,
        inY,
        160,
        20,
        LiteralText.EMPTY,
        press
    ) {
        private fun getBountyData(): BountyData {
            return BountyData[boardHandler.inventory.getStack(dataIndex)]
        }

        // We need to do custom text here, in case objective requires higher than stack size
        fun renderStackText(text: Text, rx: Int, ry: Int) {
            val matrixStack = MatrixStack()
            matrixStack.translate(0.0, 0.0, (this.zOffset + 200.0f).toDouble())

            val chars = text.asString().length

            // Only apply scaling on amounts higher than 2
            val charScaling = (chars - 1).coerceAtLeast(1)

            if (charScaling > 1) {
                matrixStack.scale(.5f, .5f, 1f)
                matrixStack.translate(rx.toDouble(), ry.toDouble(), 0.0)
            }

            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

            textRenderer.draw(
                text,
                (rx + (17 * charScaling) - textRenderer.getWidth(text)).toFloat(),
                (ry + 9 * charScaling).toFloat(),
                16777215,
                true,
                matrixStack.peek().model,
                immediate,
                false,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            )
            immediate.draw()
        }

        private fun renderEntry(entry: BountyDataEntry, rx: Int, ry: Int, color: Int = 0xFFFFFF) {
            when (entry.type) {
                BountyType.ITEM -> {
                    val stack = ItemLogic(entry).itemStack.apply {
                        count = entry.amount
                    }
                    itemRenderer.renderInGui(stack, rx, ry)
                    renderStackText(textLiteral(entry.amount.toString()) {
                        color(color)
                    }, rx, ry)
                }
            }
        }

        private fun renderDataTooltip(matrices: MatrixStack, data: BountyDataEntry, rx: Int, ry: Int, mouseX: Int, mouseY: Int, iconSize: Int) {
            if (mouseX in rx+1 until rx+iconSize && mouseY in ry+1 until ry+iconSize) {
                this@BoardScreen.renderTooltip(
                    matrices,
                    data.textBoard(MinecraftClient.getInstance().player!!),
                    mouseX,
                    mouseY
                )
            }
        }

        override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
            super.renderButton(matrices, mouseX, mouseY, delta)
            //println(isHovered)
            val data = getBountyData()

            val ry = y + 1
            val iconSize = 20

            data.objectives.forEachIndexed { index, obj ->
                val rx = x + 3 + (iconSize * index)
                renderEntry(obj, rx, ry)
                renderDataTooltip(matrices, obj, rx, ry, mouseX, mouseY, iconSize)
            }

            data.rewards.forEachIndexed { index, rew ->
                val rx = (x + width - iconSize - (iconSize * index))
                renderEntry(rew, rx, ry, rew.rarity.color.colorValue!!)
                renderDataTooltip(matrices, rew, rx, ry, mouseX, mouseY, iconSize)
            }

        }

    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2

        // TODO take up to a certain amount, depending on scroll

        /*
        for (i in 0 until 6) {
            addDrawableChild(
                WidgetButtonBounty(i, x + 5, 20 * i + y + 18) {
                    println("Pressed it!")
                }
            )
        }
         */


        //addDrawableChild()

    }

    companion object {

        private val TEXTURE = Bountiful.id("textures/gui/container/new_board.png")
        private val WANDER = Identifier("textures/gui/container/villager2.png")

        private val BOARD_SHEET = KSpriteGrid(TEXTURE, texWidth = 512, texHeight = 256)
        private val BOARD_BG = BOARD_SHEET.Sprite(0f, 0f, 348, 146)

        private val WANDER_SHEET = KSpriteGrid(WANDER, texWidth = 512, texHeight = 256)
        private val BAR_BG = WANDER_SHEET.Sprite(0f, 186f, 102, 5)
        private val BAR_FG = WANDER_SHEET.Sprite(0f, 191f, 102, 5)

    }
}

