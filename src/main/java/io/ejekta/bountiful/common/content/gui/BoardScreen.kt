package io.ejekta.bountiful.common.content.gui

import net.minecraft.client.util.math.MatrixStack

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.common.Bountiful

import net.minecraft.entity.player.PlayerInventory

import net.minecraft.screen.ScreenHandler

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.text.Text


class BoardScreen(handler: ScreenHandler?, inventory: PlayerInventory?, title: Text?) :
    HandledScreen<ScreenHandler?>(handler, inventory, title) {

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(TEXTURE)
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    companion object {
        private val TEXTURE = Bountiful.id("textures/gui/container/bounty_board.png")
    }
}

