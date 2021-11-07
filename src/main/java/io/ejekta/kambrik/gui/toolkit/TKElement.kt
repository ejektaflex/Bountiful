package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import kotlinx.serialization.Transient

abstract class TKElement(var x: Int, var y: Int, width: Int, height: Int) : KWidget(width, height) {
    var name: String = this::class.simpleName ?: "Element"
    @Transient var isWidgetHovered = false
    @Transient var lastClickedButton: Int? = null

    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return dsl {
            area(this@TKElement) {
                isWidgetHovered = isHovered
            }
        }
    }

    override fun onClickDown(relX: Int, relY: Int, button: Int) {
        lastClickedButton = button
    }

}