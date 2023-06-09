package io.ejekta.bountiful.kambrik.gui

import io.ejekta.bountiful.kambrik.KambrikScreenCommon
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity


class KGui(
    val screen: Screen,
    private val coordFunc: () -> Pair<Int, Int>,
    var x: Int = 0,
    var y: Int = 0,
    private val func: KGuiDsl.() -> Unit = {}
) {

    val logic: KambrikScreenCommon
        get() = screen as KambrikScreenCommon

    val entityRenderCache = mutableMapOf<EntityType<*>, LivingEntity>()

    fun draw(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float? = null) {
        logic.boundsStack.clear()
        logic.areaClickStack.clear()
        val toDraw = logic.modalStack.lastOrNull() ?: func // Draw top of modal stack, or func if not exists
        val dsl = KGuiDsl(this, context, mouseX, mouseY, delta).draw(toDraw)
    }

    fun pushModal(dsl: KGuiDsl.() -> Unit) = logic.modalStack.add(dsl)

    fun absX(relX: Int = 0) = x + coordFunc().first + relX

    fun absY(relY: Int = 0) = y + coordFunc().second + relY

}