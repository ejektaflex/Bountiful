package ejektaflex.bountiful.api.logic.pickable

import ejektaflex.bountiful.api.logic.ItemRange


open class PickableEntry(var content: String, var amount: ItemRange, var unitWorth: Int) {

    // Get around ugly JSON serialization of IntRange for our purposes
    constructor(inString: String, amount: IntRange, worth: Int) : this(inString, ItemRange(amount), worth)

    val randCount: Int
        get() = (amount.min..amount.max).random()

    override fun toString(): String {
        return "Pickable [Item: $content, Amount: ${amount.min..amount.max}, Unit Worth: $unitWorth]"
    }

    fun pick(): IPickedEntry {
        return PickedEntry(content, randCount).typed()
    }

}