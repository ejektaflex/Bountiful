package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid

class KScrollbarVertical(
    scrollHeight: Int,
    knobSprite: KSpriteGrid.Sprite,
    backgroundColor: Int? = null
) : KScrollbar(knobSprite, backgroundColor) {
    override val height = scrollHeight
    override val width = knobSprite.width

    override val scrollbarSize: Int
        get() = height

    override val knobSize: Int
        get() = knobSprite.height

    override fun onDraw(dsl: KGuiDsl) = dsl {
        super.onDraw(this)
        val relY = dsl.mouseY - dsl.ctx.absY()
        val newPos = knobPos(relY)

        if (isDragged) {
            sprite(knobSprite, y = newPos)
            dragStart = newPos
        } else {
            sprite(knobSprite, y = dragStart)
        }
    }
}