package io.ejekta.bountiful.client

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemLogic
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.kambrik.text.textLiteral
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
        backgroundWidth = 199
        backgroundHeight = 180
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        drawSimpleCenteredImage(matrices, TEXTURE, backgroundWidth, backgroundHeight)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawLevelInfo(matrices, x, y)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {

        textRenderer.draw(matrices, title, titleX.toFloat() - 29, titleY.toFloat() + 1, 0xEADAB5)

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

        // Bar itself
        drawTexture(matrices, x + 136, y + 16, zOffset, 0.0f, 186.0f, 102, 5, 256, 512)

        val levelData = BoardBlockEntity.levelProgress(boardHandler.totalDone)

        val percentDone = (levelData.second.toDouble() / levelData.third * 100).toInt()

        // Filling bar
        drawTexture(matrices, x + 136, y + 16, zOffset, 0.0f, 191.0f, percentDone + 1, 5, 256, 512)

        DrawableHelper.drawCenteredText(matrices, textRenderer, textLiteral(levelData.first.toString()) {
            color(0xabff7a)
        }, 340, 50, 0xFFFFFF)



    }

    inner class WidgetButtonBounty(var dataIndex: Int, inX: Int, inY: Int, press: PressAction) : ButtonWidget(inX, inY, 180, 20, LiteralText.EMPTY, press) {
        fun getBountyData(): BountyData {
            return BountyData[boardHandler.inventory.getStack(dataIndex)]
        }


        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            super.renderButton(matrices, mouseX, mouseY, delta)
            val data = getBountyData()


            data.objectives.forEachIndexed { index, obj ->
                when (obj.type) {
                    BountyType.ITEM -> {
                        val stack = ItemLogic(obj).itemStack.apply {
                            count = obj.amount
                        }

                        val rx = x + 3 + (20 * index)
                        val ry = y + 1

                        itemRenderer.renderInGui(stack, rx, ry)

                        val matrixStack = MatrixStack()
                        val string = obj.amount.toString()
                        matrixStack.translate(0.0, 0.0, (this.zOffset + 200.0f).toDouble())
                        val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
                        textRenderer.draw(
                            string,
                            (rx + 19 - 2 - textRenderer.getWidth(string)).toFloat(),
                            (ry + 6 + 3).toFloat(),
                            16777215,
                            true,
                            matrixStack.peek().model,
                            immediate,
                            false,
                            0,
                            LightmapTextureManager.MAX_LIGHT_COORDINATE
                        )
                        immediate.draw()

                        //itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + (20 * index), y + 1)
                    }
                }
            }

        }

        init {
            println("Hai!")
        }

    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2

        // TODO take up to a certain amount, depending on scroll

        for (i in 0 until 7) {
            addDrawableChild(
                WidgetButtonBounty(i, 50, 20 * i) {
                    println("Pressed it!")
                }
            )
        }

    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/bounty_board.png")
        private val WANDER = Identifier("textures/gui/container/villager2.png")
    }
}

