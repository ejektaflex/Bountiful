package io.ejekta.kambrik.gui.widgets

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget

class KSimpleWidget(w: Int, h: Int) : KWidget(w, h) {
    var canClickThrough: Boolean = false
    var onDrawFunc: KGuiDsl.() -> Unit = {}
    var onHoverFunc: (relX: Int, relY: Int) -> Unit = { _, _ -> }
    var onClickFunc: (relX: Int, relY: Int, button: Int) -> Unit = { _, _, _ -> }

    fun create(func: KSimpleWidget.() -> Unit): KSimpleWidget = apply(func)

    override fun canClickThrough() = canClickThrough

    override fun onHover(relX: Int, relY: Int) = onHoverFunc(relX, relY)

    override fun onDraw(dsl: KGuiDsl) = dsl.apply(onDrawFunc)

    override fun onClickDown(relX: Int, relY: Int, button: Int) = onClickFunc(relX, relY, button)

}