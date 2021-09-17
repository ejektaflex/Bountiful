package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.ext.fapi.textRenderer
import io.ejekta.kambrik.gui.KambrikSpriteGrid
import io.ejekta.kambrik.text.KambrikTextBuilder
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

data class KambrikGuiDsl(val ctx: KGui, val matrices: MatrixStack, val mouseX: Int, val mouseY: Int, val delta: Float?) {

    operator fun invoke(func: KambrikGuiDsl.() -> Unit) = apply(func)

    fun offset(x: Int, y: Int, func: KambrikGuiDsl.() -> Unit) {
        ctx.x += x
        ctx.y += y
        apply(func)
        ctx.x -= x
        ctx.y -= y
    }

    fun rect(x: Int, y: Int, w: Int, h: Int, color: Int = 0xFFFFFF, func: KambrikGuiDsl.() -> Unit = {}) {
        offset(x, y) {
            val sx = ctx.absX(x)
            val sy = ctx.absY(y)
            DrawableHelper.fill(matrices, sx, sy, sx + w, sy + h, color)
            this.apply(func)
        }
    }

    fun textCentered(x: Int, y: Int, text: Text) {
        DrawableHelper.drawCenteredText(
            matrices,
            ctx.screen.textRenderer,
            text,
            ctx.absX(x),
            ctx.absY(y),
            0xFFFFFF
        )
    }

    fun textCentered(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<LiteralText>.() -> Unit) {
        DrawableHelper.drawCenteredText(
            matrices,
            ctx.screen.textRenderer,
            textLiteral("", textDsl),
            ctx.absX(x),
            ctx.absY(y),
            0xFFFFFF
        )
    }

    fun sprite(sprite: KambrikSpriteGrid.Sprite, x: Int = 0, y: Int = 0, w: Int = sprite.width, h: Int = sprite.height) {
        sprite.draw(
            ctx.screen,
            matrices,
            ctx.absX(x),
            ctx.absY(y),
            w,
            h
        )
    }

    fun widget(kWidget: KWidget) {
        kWidget.onDraw(this)
    }

    fun isHovered(w: Int, h: Int): Boolean {
        return isHovered(0, 0, w, h)
    }

    fun isHovered(startX: Int, startY: Int, w: Int, h: Int): Boolean {
        return mouseX >= ctx.absX(startX) && mouseX <= ctx.absX(startX + w)
                && mouseY >= ctx.absY(startY) && mouseY <= ctx.absY(startY + h)
    }

}