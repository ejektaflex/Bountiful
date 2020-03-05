package ejektaflex.bountiful.generic

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow

interface IWeighted {
    var weight: Int

    fun normalizedWeight(exp: Double): Int {
        return max(1, ceil(weight.toDouble().pow(exp)).toInt())
    }

}
