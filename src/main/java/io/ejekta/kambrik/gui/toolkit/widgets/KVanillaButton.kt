package io.ejekta.kambrik.gui.toolkit.widgets

import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.toolkit.KWidget
import io.ejekta.kambrik.gui.toolkit.KGuiDsl
import net.minecraft.util.Identifier


/**
 * A vanilla style button.
 * @param width The width of the button - up to 395.
 */
open class KVanillaButton(override val width: Int = 50) : KWidget(width, 20) {

    var disabled = false

    init {
        if (width > 395) {
            //Kambrik.Logger.warn("Vanilla Style Buttons made with Kambrik can be at most 395px in width!")
        }
    }

    override fun onDraw(gui: KGuiDsl): KGuiDsl {
        val maxSprite = (width).coerceAtMost(198)
        return gui {
            val toDraw = if (isHovered(width, height)) HOVERED else {
                if (!disabled) DEFAULT else DISABLED
            }
            sprite(toDraw, x = width - width.coerceAtMost(200) + 1)
            sprite(toDraw, w = maxSprite)
        }
    }

    companion object {
        val SHEET = KSpriteGrid(Identifier("textures/gui/widgets.png"), 256, 256)
        val DISABLED = SHEET.Sprite(0f, 46f, 200, 20)
        val DEFAULT = SHEET.Sprite(0f, 66f, 200, 20)
        val HOVERED = SHEET.Sprite(0f, 86f, 200, 20)
    }
}


