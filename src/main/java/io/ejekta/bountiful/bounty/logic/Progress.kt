package io.ejekta.bountiful.bounty.logic

import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting
import java.math.RoundingMode

data class Progress(val current: Double, val goal: Double, private val precision: Int = 0) {

    constructor(currentInt: Int, goalInt: Int, precision: Int = 0) : this(currentInt.toDouble(), goalInt.toDouble(), precision)

    private fun isComplete() = current >= goal

    val color: Formatting
        get() = if (isComplete()) Formatting.GREEN else Formatting.RED

    private val Double.preciseUp: String
        get() = toBigDecimal().setScale(precision, RoundingMode.UP).toString()

    private val Double.preciseDown: String
        get() = toBigDecimal().setScale(precision, RoundingMode.DOWN).toString()

    val neededText: MutableText
        get() = LiteralText(" (${current.preciseDown}/${goal.preciseDown})")

    val givingText: MutableText
        get() = LiteralText("${goal.preciseUp}x ")

}