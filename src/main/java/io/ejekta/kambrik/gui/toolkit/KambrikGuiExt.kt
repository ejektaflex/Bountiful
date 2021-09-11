package io.ejekta.kambrik.gui.toolkit

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

fun kambrikGui(screen: Screen, matrices: MatrixStack, coordFunc: () -> Pair<Int, Int>, func: KambrikGuiDsl.() -> Unit) {
    KambrikGuiDsl(ctx = KambrikGuiContext(screen, matrices, coordFunc)).apply(func)
}