package ejektaflex.bountiful.api.generic

import com.google.gson.annotations.Expose

// A more gson serialization friendly IntRange
data class ItemRange(@Expose var min: Int = 0, @Expose var max: Int = Int.MAX_VALUE) {
    constructor(range: IntRange) : this(range.first, range.last)

    fun toIntRange(): IntRange {
        return min..max
    }

    override fun toString(): String {
        return "[$min..$max]"
    }

}