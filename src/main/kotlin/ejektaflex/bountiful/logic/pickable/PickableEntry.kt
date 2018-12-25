package ejektaflex.bountiful.logic.pickable

import ejektaflex.bountiful.ext.toItemStack
import ejektaflex.bountiful.logic.ItemRange
import net.minecraft.item.ItemStack

class PickableEntry(var itemString: String, var amount: ItemRange, var unitWorth: Int) {

    // Get around ugly JSON serialization of IntRange for our purposes
    constructor(inString: String, amount: IntRange, worth: Int) : this(inString, ItemRange(amount), worth)

    val randCount: Int
        get() = (amount.min..amount.max).random()

    val itemStack: ItemStack?
        get() = itemString.toItemStack

    override fun toString(): String {
        return "Pickable [Item: $itemString, Amount: ${amount.min..amount.max}, Unit Worth: $unitWorth]"
    }

}