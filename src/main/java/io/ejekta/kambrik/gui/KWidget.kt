package io.ejekta.kambrik.gui

import io.ejekta.kambrik.gui.KGuiDsl

open class KWidget(
    open val width: Int = 0,
    open val height: Int = 0,
) {

    open fun onClick(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    open fun onRelease(relX: Int, relY: Int, button: Int) {
        // No-op
    }

    open fun onHover(relX: Int, relY: Int) {
        // No-op
    }

    open fun onMouseMoved(relX: Int, relY: Int) {
        // No-op
    }

    open fun onDraw(dsl: KGuiDsl): KGuiDsl {
        /* No-op
        return dsl {
            ...
        }
         */
        return dsl
    }

}