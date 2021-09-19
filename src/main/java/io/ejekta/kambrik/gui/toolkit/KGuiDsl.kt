package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.ext.fapi.itemRenderer
import io.ejekta.kambrik.ext.fapi.textRenderer
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.text.KambrikTextBuilder
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

data class KGuiDsl(val ctx: KGui, val matrices: MatrixStack, val mouseX: Int, val mouseY: Int, val delta: Float?) {

    operator fun invoke(func: KGuiDsl.() -> Unit) = apply(func)

    fun offset(x: Int, y: Int, func: KGuiDsl.() -> Unit) {
        ctx.x += x
        ctx.y += y
        apply(func)
        ctx.x -= x
        ctx.y -= y
    }

    fun rect(x: Int, y: Int, w: Int, h: Int, color: Int = 0xFFFFFF, func: KGuiDsl.() -> Unit = {}) {
        offset(x, y) {
            val sx = ctx.absX(x)
            val sy = ctx.absY(y)
            DrawableHelper.fill(matrices, sx, sy, sx + w, sy + h, color)
            apply(func)
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

    fun itemStackIcon(stack: ItemStack, x: Int = 0, y: Int = 0) {
        ctx.screen.itemRenderer.renderInGui(stack, ctx.absX(x), ctx.absY(y))
    }

    fun onHoverArea(x: Int = 0, y: Int = 0, w: Int = 0, h: Int = 0, func: KGuiDsl.() -> Unit) {
        if (KRect.isInside(mouseX, mouseY, x, y, w, h)) {
            apply(func)
        }
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

    fun sprite(sprite: KSpriteGrid.Sprite, x: Int = 0, y: Int = 0, w: Int = sprite.width, h: Int = sprite.height) {
        sprite.draw(
            ctx.screen,
            matrices,
            ctx.absX(x),
            ctx.absY(y),
            w,
            h
        )
    }

    fun widget(kWidget: KWidget, relX: Int = 0, relY: Int = 0) {
        offset(relX, relY) {
            kWidget.onDraw(this)
            val boundsRect = KRect(
                ctx.absX(), ctx.absY(), kWidget.width, kWidget.height
            )
            if (boundsRect.isInside(mouseX, mouseY)) {
                // Run hover event
                kWidget.onHover(mouseX - boundsRect.x, mouseY - boundsRect.y)
                // Add to stack for later event handling
                ctx.screen.boundsStack.add(0, kWidget to boundsRect)
            }
        }
    }

    fun isHovered(w: Int, h: Int): Boolean {
        return isHovered(0, 0, w, h)
    }

    fun isHovered(startX: Int, startY: Int, w: Int, h: Int): Boolean {
        return mouseX >= ctx.absX(startX) && mouseX <= ctx.absX(startX + w)
                && mouseY >= ctx.absY(startY) && mouseY <= ctx.absY(startY + h)
    }

}