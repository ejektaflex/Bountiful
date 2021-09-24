package io.ejekta.kambrik.gui

import io.ejekta.kambrik.ext.fapi.itemRenderer
import io.ejekta.kambrik.ext.fapi.textRenderer
import io.ejekta.kambrik.text.KambrikTextBuilder
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import kotlin.math.max

data class KGuiDsl(val ctx: KGui, val matrices: MatrixStack, val mouseX: Int, val mouseY: Int, val delta: Float?) {

    private val frameDeferredTasks = mutableListOf<KGuiDsl.() -> Unit>()

    operator fun invoke(func: KGuiDsl.() -> Unit) = apply(func)

    fun draw(func: KGuiDsl.() -> Unit): KGuiDsl {
        apply(func)
        doLateDeferral()
        return this
    }

    private fun defer(func: KGuiDsl.() -> Unit) {
        frameDeferredTasks.add(func)
    }

    private fun doLateDeferral() {
        frameDeferredTasks.forEach { func -> apply(func) }
        frameDeferredTasks.clear()
    }

    fun offset(x: Int, y: Int, func: KGuiDsl.() -> Unit) {
        ctx.x += x
        ctx.y += y
        apply(func)
        ctx.x -= x
        ctx.y -= y
    }

    fun rect(x: Int, y: Int, w: Int, h: Int, color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
        offset(x, y) {
            val sx = ctx.absX()
            val sy = ctx.absY()
            DrawableHelper.fill(matrices, sx, sy, sx + w, sy + h, (alpha shl 24) + color)
            apply(func)
        }
    }

    fun rect(w: Int, h: Int, color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
        rect(0, 0, w, h, color, alpha, func)
    }

    fun itemStackIcon(stack: ItemStack, x: Int = 0, y: Int = 0) {
        ctx.screen.itemRenderer.renderInGui(stack, ctx.absX(x), ctx.absY(y))
    }

    fun itemStackOverlay(stack: ItemStack, x: Int = 0, y: Int = 0) {
        ctx.screen.itemRenderer.renderGuiItemOverlay(ctx.screen.textRenderer, stack, x, y)
    }

    fun itemStack(stack: ItemStack, x: Int = 0, y: Int = 0) {
        itemStackIcon(stack, x, y)
        itemStackOverlay(stack, x, y)
    }

    fun itemStackWithTooltip(stack: ItemStack, x: Int, y: Int) {
        itemStack(stack, x, y)
        onHover(x, y, 18, 18) {
            tooltip(ctx.screen.getTooltipFromItem(stack))
        }
    }

    fun onHover(x: Int, y: Int, w: Int, h: Int, func: KGuiDsl.() -> Unit) {
        if (KRect.isInside(mouseX, mouseY, ctx.absX(x), ctx.absY(y), w, h)) {
            apply(func)
        }
    }

    fun onHover(w: Int, h: Int, func: KGuiDsl.() -> Unit) {
        onHover(0, 0, w, h, func)
    }

    fun tooltip(texts: List<Text>) {
        defer {
            ctx.screen.renderTooltip(
                matrices,
                texts,
                mouseX,
                mouseY
            )
        }
    }

    fun tooltip(func: KambrikTextBuilder<LiteralText>.() -> Unit) {
        tooltip(listOf(textLiteral("", func)))
    }

    fun text(x: Int, y: Int, text: Text) {
        ctx.screen.textRenderer.drawWithShadow(matrices, text, ctx.absX(x).toFloat(), ctx.absY(y).toFloat(), 0xFFFFFF)
    }

    fun text(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<LiteralText>.() -> Unit) {
        text(x, y, textLiteral("", textDsl))
    }

    fun textNoShadow(x: Int, y: Int, text: Text) {
        ctx.screen.textRenderer.draw(matrices, text, ctx.absX(x).toFloat(), ctx.absY(y).toFloat(), 0xFFFFFF)
    }

    fun textNoShadow(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<LiteralText>.() -> Unit) {
        textNoShadow(x, y, textLiteral("", textDsl))
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
        textCentered(x, y, textLiteral("", textDsl))
    }

    fun textImmediate(x: Int, y: Int, text: Text) {
        val matrixStack = MatrixStack()
        matrixStack.translate(0.0, 0.0, (ctx.screen.zOffset + 200.0f).toDouble())
        val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

        ctx.screen.textRenderer.draw(
            text,
            (ctx.absX(x) + ctx.screen.textRenderer.getWidth(text)).toFloat(),
            ctx.absY(y).toFloat(),
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

    fun spriteCentered(sprite: KSpriteGrid.Sprite, func: KGuiDsl.() -> Unit) {
        offset(ctx.screen.width / 2 - sprite.width / 2, ctx.screen.height / 2 - sprite.height / 2) {
            sprite(sprite)
            apply(func)
        }
    }

    fun livingEntity(entity: LivingEntity, x: Int = 0, y: Int = 0, size: Double = 20.0) {
        val dims = entity.getDimensions(entity.pose)
        val maxDim = (1 / max(dims.height, dims.width) * 1 * size).toInt().coerceAtLeast(1)
        InventoryScreen.drawEntity(
            ctx.absX(x),
            ctx.absY(y),
            maxDim,
            ctx.absX(x) - mouseX.toFloat(),
            ctx.absY(y) - mouseY.toFloat(),
            entity
        )
    }

    fun livingEntity(entityType: EntityType<out LivingEntity>, x: Int = 0, y: Int = 0, size: Double = 20.0) {
        val entity = ctx.entityRenderCache.getOrPut(entityType) {
            entityType.create(MinecraftClient.getInstance().world) as LivingEntity
        }
        livingEntity(entity, x, y, size)
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
            }
            // Add to stack for later event handling
            ctx.logic.boundsStack.add(0, kWidget to boundsRect)
        }
    }

    fun isHovered(w: Int, h: Int): Boolean {
        return isHovered(0, 0, w, h)
    }

    fun isHovered(startX: Int, startY: Int, w: Int, h: Int): Boolean {
        return mouseX >= ctx.absX(startX) && mouseX <= ctx.absX(startX + w)
                && mouseY >= ctx.absY(startY) && mouseY <= ctx.absY(startY + h)
    }

    fun isHovered(widget: KWidget): Boolean {
        return isHovered(widget.width, widget.height)
    }

    fun area(w: Int, h: Int, func: AreaDsl.() -> Unit) {
        areaDsl.adjusted(w, h, func)
    }

    fun area(relX: Int, relY: Int, w: Int, h: Int, func: AreaDsl.() -> Unit) {
        offset(relX, relY) {
            area(w, h, func)
        }
    }

    fun area(widget: KWidget, func: AreaDsl.() -> Unit) {
        areaDsl.adjusted(widget.width, widget.height, func)
    }

    private val areaDsl = AreaDsl(0, 0)

    inner class AreaDsl(var w: Int, var h: Int) {

        internal fun adjusted(newW: Int, newH: Int, func: AreaDsl.() -> Unit) {
            val oldW = w
            val oldH = h
            w = newW
            h = newH
            apply(func)
            w = oldW
            h = oldH
        }

        fun rect(color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
            rect(w, h, color, alpha, func)
        }

        fun onHover(func: KGuiDsl.() -> Unit) {
            onHover(w, h, func)
        }

        val isHovered: Boolean
            get() = isHovered(w, h)

        fun textCentered(y: Int, text: Text) {
            textCentered(w / 2, y, text)
        }

        fun widgetCentered(widget: KWidget, func: KGuiDsl.() -> Unit = {}) {
            offset(w / 2 - widget.width / 2, h / 2 - widget.height / 2) {
                widget(widget)
                apply(func)
            }
        }

    }

}