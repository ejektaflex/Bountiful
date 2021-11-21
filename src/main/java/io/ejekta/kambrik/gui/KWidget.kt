package io.ejekta.kambrik.gui

interface KWidget {

    val width: Int

    val height: Int

    /**
     * A callback that allows the widget to draw to the screen.
     */
    fun onDraw(dsl: KGuiDsl): KGuiDsl {
        /* No-op
        return dsl {
            ...
        }
         */
        return dsl
    }
}