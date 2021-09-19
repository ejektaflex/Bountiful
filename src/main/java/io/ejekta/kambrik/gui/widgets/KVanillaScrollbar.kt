package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KWidget

class KVanillaScrollbar(
    override val height: Int,
    private val knobSprite: KSpriteGrid.Sprite,
) : KWidget(knobSprite.width, height) {

    init {
        if (knobSprite.height > height) {
            throw Exception("Scrollbar knob height cannot be taller than the scrollbar itself! (${knobSprite.height} > $height)")
        }
    }

    private val moveRange = 0 .. height - knobSprite.height
    private var dragStart = 0
    private var isMoving = false

    var percent: Double = 0.0
        private set

    private fun knobPos(relY: Int): Int {
        return (relY - (knobSprite.height / 2)).coerceIn(moveRange)
    }

    override fun onClick(relX: Int, relY: Int, button: Int) {
        isMoving = true
    }

    override fun onRelease(relX: Int, relY: Int, button: Int) {
        isMoving = false
        dragStart = knobPos(relY)
    }

    override fun onDraw(dsl: KGuiDsl) = dsl {
        val relY = dsl.mouseY - dsl.ctx.absY()
        val newPos = knobPos(relY)

        if (isMoving) {
            sprite(knobSprite, y = newPos)
            percent = newPos.toDouble() / moveRange.last
        } else {
            sprite(knobSprite, y = dragStart)
        }
    }
}