package io.ejekta.bountiful.client

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemLogic
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class BoardScreen(handler: ScreenHandler?, inventory: PlayerInventory, title: Text) :
    HandledScreen<ScreenHandler?>(handler, inventory, title) {

    val boardHandler: BoardScreenHandler
        get() = handler as BoardScreenHandler

    init {
        backgroundWidth = 348
        backgroundHeight = 146
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        drawSimpleCenteredImage(matrices, TEXTURE, backgroundWidth, backgroundHeight, 512, 256)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawLevelInfo(matrices, x, y)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {

        // TODO draw this centered!
        textRenderer.draw(matrices, title, titleX.toFloat() - 88, titleY.toFloat() + 1, 0xEADAB5)

        /*
        val lvl = (screenHandler as? BoardScreenHandler)?.level ?: 0

        textRenderer.draw(
            matrices, LiteralText("Reputation: ")
                .append(LiteralText("$lvl").formatted(BountyRarity.forReputation(lvl).color)),
            playerInventoryTitleX.toFloat(), playerInventoryTitleY.toFloat() + 1, 0xEADAB5
        )

         */

        //super.drawForeground(matrices, mouseX, mouseY)
    }

    private fun drawLevelInfo(matrices: MatrixStack, x: Int, y: Int) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderTexture(0, WANDER)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        val barX = 210
        val barY = 56

        // Bar itself
        drawTexture(matrices, x + barX, y + barY, zOffset, 0.0f, 186.0f, 102, 5, 256, 512)

        val levelData = BoardBlockEntity.levelProgress(boardHandler.totalDone)

        val percentDone = (levelData.second.toDouble() / levelData.third * 100).toInt()

        // Filling bar
        drawTexture(matrices, x + barX, y + barY, zOffset, 0.0f, 191.0f, percentDone + 1, 5, 256, 512)

        DrawableHelper.drawCenteredText(matrices, textRenderer, textLiteral(levelData.first.toString()) {
            color(0xabff7a)
        }, x + barX - 10, y + barY - 2, 0xFFFFFF)



    }

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

        override fun renderTooltip(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
            if (hovered) {
                /*
                this@BoardScreen.renderTooltip(
                    matrices,
                    getBountyData().tooltipInfo(MinecraftClient.getInstance().world!!),
                    mouseX,
                    mouseY
                )

                 */
            }
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

        for (i in 0 until 6) {
            addDrawableChild(
                WidgetButtonBounty(i, x + 5, 20 * i + y + 18) {
                    println("Pressed it!")
                }
            )
        }

    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/new_board.png")
        private val WANDER = Identifier("textures/gui/container/villager2.png")
    }
}

