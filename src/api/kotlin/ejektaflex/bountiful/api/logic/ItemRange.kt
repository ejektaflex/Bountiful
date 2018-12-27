package ejektaflex.bountiful.api.logic

// A more gson serialization friendly IntRange
data class ItemRange(var min: Int, var max: Int) {
    constructor(range: IntRange) : this(range.first, range.last)
}