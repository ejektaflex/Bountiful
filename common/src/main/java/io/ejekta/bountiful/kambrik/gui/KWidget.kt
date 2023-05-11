package io.ejekta.bountiful.kambrik.gui

interface KWidget {

    val width: Int

    val height: Int

    fun doDraw(dsl: KGuiDsl) {
        dsl {
            area(width, height) {
                onDraw(this)
            }
        }
    }

    /**
     * A callback that allows the widget to draw to the screen.
     */
    fun onDraw(area: KGuiDsl.AreaDsl) {
        /* No-op
        return dsl {
            ...
        }
         */
    }
}