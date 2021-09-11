package io.ejekta.kambrik.gui.toolkit

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class KGui(
    val screen: Screen,
    val coordFunc: () -> Pair<Int, Int>,
    val x: Int = 0,
    val y: Int = 0,
    val func: KambrikGuiDsl.() -> Unit = {}
) {

    operator fun invoke(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float? = null) {
        KambrikGuiDsl(this, matrices, mouseX, mouseY, delta).apply(func)
    }

    operator fun plus(rel: Pair<Int, Int>) = KGui(
        screen,
        coordFunc,
        x + rel.first,
        y + rel.second
    )

    fun absX(relX: Int) = x + coordFunc().first + relX

    fun absY(relY: Int) = y + coordFunc().second + relY

}