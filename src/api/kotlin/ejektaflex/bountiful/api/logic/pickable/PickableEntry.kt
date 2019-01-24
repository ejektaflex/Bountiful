package ejektaflex.bountiful.api.logic.pickable

import ejektaflex.bountiful.api.data.IValidatable
import ejektaflex.bountiful.api.data.IWeighted
import ejektaflex.bountiful.api.logic.ItemRange
import ejektaflex.bountiful.api.logic.picked.IPickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntry


open class PickableEntry(var content: String, var amount: ItemRange, var unitWorth: Int, override var weight: Int = 100) : IWeighted, IValidatable {

    // Get around ugly JSON serialization of IntRange for our purposes
    constructor(inString: String, amount: IntRange, worth: Int, weight: Int = 100) : this(inString, ItemRange(amount), worth, weight)

    val randCount: Int
        get() = (amount.min..amount.max).random()

    override fun toString(): String {
        return "Pickable [Item: $content, Amount: ${amount.min..amount.max}, Unit Worth: $unitWorth]"
    }

    fun pick(): IPickedEntry {
        return PickedEntry(content, randCount).typed()
    }

    override fun isValid(): Boolean {
        return pick().contentObj != null
    }


}