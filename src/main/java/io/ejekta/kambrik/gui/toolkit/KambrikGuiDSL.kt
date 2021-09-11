package io.ejekta.kambrik.gui.toolkit

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.kambrik.ext.fapi.textRenderer
import io.ejekta.kambrik.gui.KambrikSpriteGrid
import io.ejekta.kambrik.text.KambrikTextBuilder
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.text.BaseText
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

data class KambrikGuiDSL(val ctx: KambrikGuiContext) {

    fun offset(x: Int, y: Int, func: KambrikGuiDSL.() -> Unit) {
        KambrikGuiDSL(ctx + (x to y)).apply(func)
    }

    fun textCentered(x: Int, y: Int, text: Text) {
        DrawableHelper.drawCenteredText(ctx.matrices, ctx.screen.textRenderer, text, ctx.absX(x), ctx.absY(y), 0xFFFFFF)
    }

    fun textCentered(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<LiteralText>.() -> Unit) {
        DrawableHelper.drawCenteredText(ctx.matrices, ctx.screen.textRenderer, textLiteral("", textDsl), x, y, 0xFFFFFF)
    }

    fun sprite(sprite: KambrikSpriteGrid.KambrikSprite, x: Int = 0, y: Int = 0, w: Int = sprite.width, h: Int = sprite.height) {
        sprite.draw(ctx.screen, ctx.matrices, ctx.absX(x), ctx.absY(y), w, h)
    }

}