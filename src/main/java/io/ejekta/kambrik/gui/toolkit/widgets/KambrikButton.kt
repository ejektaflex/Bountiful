package io.ejekta.kambrik.gui.toolkit.widgets

import io.ejekta.kambrik.gui.KambrikSpriteGrid
import io.ejekta.kambrik.gui.toolkit.KWidget
import io.ejekta.kambrik.gui.toolkit.KambrikGuiDsl
import net.minecraft.util.Identifier

class KambrikButton : KWidget(50, 20) {
    override fun onDraw(gui: KambrikGuiDsl) {
        gui {
            sprite(if (isHovered(width, height)) FG else BG, w = width)
        }
    }

    companion object {
        val SHEET = KambrikSpriteGrid(Identifier("textures/gui/widgets.png"), 256, 256)
        val BG = SHEET.Sprite(0f, 46f, 200, 20)
        val FG = SHEET.Sprite(0f, 66f, 200, 20)
    }
}


