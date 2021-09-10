package io.ejekta.kambrik.gui.toolkit

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

@JvmRecord
data class KambrikGuiContext(
    val screen: Screen,
    val matrices: MatrixStack,
    val coordFunc: () -> Pair<Int, Int>,
    val x: Int = 0,
    val y: Int = 0
) {
    operator fun plus(rel: Pair<Int, Int>) = KambrikGuiContext(
        screen,
        matrices,
        coordFunc,
        x + rel.first,
        y + rel.second
    )

    fun absX(relX: Int) = x + coordFunc().first + relX

    fun absY(relY: Int) = y + coordFunc().second + relY

}