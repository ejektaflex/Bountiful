package ejektaflex.bountiful.api.logic

// A more gson serialization friendly IntRange
data class ItemRange(var min: Int = 0, var max: Int = Int.MAX_VALUE) {
    constructor(range: IntRange) : this(range.first, range.last)

    fun toIntRange(): IntRange {
        return min..max
    }
}