package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.text.textLiteral

object KTModals {

    fun colorPicker(rect: TKRect): KGuiDsl.() -> Unit = {
        area(100, 100) {
            rect(0x0)
            text(0, 0, textLiteral("Doot!"))
        }
    }

}