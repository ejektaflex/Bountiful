package io.ejekta.kambrik.gui.toolkit.`interface`

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.toolkit.TKElement
import io.ejekta.kambrik.gui.toolkit.TKRoot
import io.ejekta.kambrik.text.textLiteral

class TreeElementDrawer(val root: TKRoot, var element: TKElement? = null, w: Int) : KWidget(w, 11) {
    override fun onDraw(dsl: KGuiDsl): KGuiDsl {
        return dsl {
            element?.let {
                // Set selection
                if (it.lastClickedButton != null) {
                    root.selected = it
                    it.lastClickedButton = null
                }
                // Show hovering
                if (it.isWidgetHovered || it == root.selected) {
                    area(width, height) {
                        rect(0x333333)
                    }
                }
                textNoShadow(2, 2, textLiteral(it.name))
            }
        }
    }
}