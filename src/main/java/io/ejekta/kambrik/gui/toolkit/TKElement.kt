package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget

abstract class TKElement(var x: Int, var y: Int, width: Int, height: Int) : KWidget(width, height) {
    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return dsl
    }
}