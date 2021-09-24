package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.gui.KGuiDsl

class TKRoot : TKElement(0, 0, 0, 0), TKElementHolder {
    override val elements = mutableListOf<TKElement>()
    override var isExpanded = true

    var selected: TKElement? = null

    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return dsl {
            for (element in elements) {
                widget(element, element.x, element.y)
            }
        }
    }
}