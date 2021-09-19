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

    private var knobLocation = 0

    private val knobSizeRange: IntRange
        get() = knobLocation until knobLocation + knobSprite.height

    var percent: Double = 0.0
        private set

    var isMoving = false

    override fun onClick(relX: Int, relY: Int, button: Int) {
        println("Click!")
        if (relY in knobSizeRange) {
            // do nothing
        } else {
            knobLocation = (relY - (knobSprite.height / 2)).coerceIn(moveRange)
        }
        isMoving = true
        //startLocation = relY
    }

    override fun onHover(relX: Int, relY: Int) {
        //println("Hover")
    }

    override fun onMouseMoved(relX: Int, relY: Int) {
        //println("Mouse moved")
    }

    override fun onRelease(relX: Int, relY: Int, button: Int) {
        isMoving = false
        knobLocation = relY
        //startLocation = relY
    }

    override fun onDraw(dsl: KGuiDsl) = dsl {
        val currLocation = dsl.mouseY - dsl.ctx.absY()
        val newPos = (currLocation - knobLocation).coerceIn(moveRange)
        percent = newPos.toDouble() / moveRange.last

        if (isMoving) {
            sprite(knobSprite, y = newPos)
        } else {
            sprite(knobSprite, y = knobLocation)
        }
    }

}