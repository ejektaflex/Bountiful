package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid

class KVanillaScrollbarHorizontal(
    scrollWidth: Int,
    knobSprite: KSpriteGrid.Sprite,
    backgroundColor: Int? = null
) : KVanillaScrollbar(knobSprite, backgroundColor) {
    override val height = knobSprite.height
    override val width = scrollWidth

    override val scrollbarSize: Int
        get() = width

    override val knobSize: Int
        get() = knobSprite.width

    override fun onDraw(dsl: KGuiDsl) = dsl {
        super.onDraw(this)
        val relX = dsl.mouseX - dsl.ctx.absX()
        val newPos = knobPos(relX)

        if (isMoving) {
            sprite(knobSprite, x = newPos)
            percent = newPos.toDouble() / moveRange.last
            dragStart = newPos
        } else {
            sprite(knobSprite, x = dragStart)
        }
    }
}