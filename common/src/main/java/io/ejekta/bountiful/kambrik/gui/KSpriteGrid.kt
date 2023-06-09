package io.ejekta.bountiful.kambrik.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

open class KSpriteGrid(val location: Identifier, val texWidth: Int, val texHeight: Int) {

    inner class Sprite(
        val u: Float = 0f,
        val v: Float = 0f,
        val width: Int,
        val height: Int
    ) {

        val grid: KSpriteGrid
            get() = this@KSpriteGrid

        fun draw(screen: Screen, context: DrawContext, x: Int, y: Int, w: Int = width, h: Int = height) {
            //RenderSystem.setShader(GameRenderer::getPositionTexProgram)
            //RenderSystem.setShaderTexture(0, location)
            //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            context.drawTexture(location, x, y, u, v, w, h, texWidth, texHeight)
        }
    }

}