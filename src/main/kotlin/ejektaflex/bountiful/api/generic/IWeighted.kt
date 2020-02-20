package ejektaflex.bountiful.api.generic

import kotlin.math.ceil
import kotlin.math.pow

interface IWeighted {
    var weight: Int

    fun normalizedWeight(exp: Double): Int {
        return ceil(weight.toDouble().pow(exp)).toInt()
    }

}
