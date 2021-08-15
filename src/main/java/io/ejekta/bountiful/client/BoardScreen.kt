package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text


class BoardScreen(handler: ScreenHandler?, inventory: PlayerInventory, title: Text) :
    HandledScreen<ScreenHandler?>(handler, inventory, title) {

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
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {

        textRenderer.draw(matrices, title, titleX.toFloat() - 29, titleY.toFloat() + 1, 0xEADAB5)

        val lvl = (screenHandler as? BoardScreenHandler)?.level ?: 0

        textRenderer.draw(
            matrices, LiteralText("Reputation: ")
                .append(LiteralText("$lvl").formatted(BountyRarity.forReputation(lvl).color)),
            playerInventoryTitleX.toFloat(), playerInventoryTitleY.toFloat() + 1, 0xEADAB5
        )

        //super.drawForeground(matrices, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/bounty_board.png")
    }
}

