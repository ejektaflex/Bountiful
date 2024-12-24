package io.ejekta.bountiful.bounty.types

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.math.RoundingMode

data class Progress(val current: Double, val goal: Double, private val precision: Int = 0) {

    constructor(currentInt: Int, goalInt: Int, precision: Int = 0) : this(currentInt.toDouble(), goalInt.toDouble(), precision)

    fun isComplete() = current >= goal

    val color: ChatFormatting
        get() = if (isComplete()) ChatFormatting.GREEN else ChatFormatting.RED

    private val Double.preciseUp: String
        get() = toBigDecimal().setScale(precision, RoundingMode.UP).toString()

    private val Double.preciseDown: String
        get() = toBigDecimal().setScale(precision, RoundingMode.DOWN).toString()

    val neededText: MutableComponent
        get() = Component.literal(" (${current.preciseDown}/${goal.preciseDown})")

    val givingText: MutableComponent
        get() = Component.literal("${goal.preciseUp}x ")

}