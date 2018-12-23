package bountiful.logic

import bountiful.ext.toItemStack
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound
import java.util.*

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