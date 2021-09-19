package io.ejekta.kambrik.gui.toolkit

import io.ejekta.kambrik.KambrikHandledScreen
import net.minecraft.client.util.math.MatrixStack


class KGui(
    val screen: KambrikHandledScreen<*>,
    val coordFunc: () -> Pair<Int, Int>,
    var x: Int = 0,
    var y: Int = 0,
    val func: KGuiDsl.() -> Unit = {}
) {

    fun draw(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float? = null) {
        screen.boundsStack.clear()
        val dsl = KGuiDsl(this, matrices, mouseX, mouseY, delta).apply(func)
    }

    fun absX(relX: Int = 0) = x + coordFunc().first + relX

    fun absY(relY: Int = 0) = y + coordFunc().second + relY

}