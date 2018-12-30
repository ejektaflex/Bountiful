package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack

class PickedEntryStack(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {


    val itemStack: ItemStack?
        get() = contentID.toItemStack

    override val content: Any?
        get() = itemStack

    override fun toString(): String {
        return amount.toString() + "x " + (itemStack?.displayName ?: "Unknown Item (Content ID: $contentID)")
    }

    override val prettyContent: String
        get() = itemStack?.displayName ?: "Unknown ItemStack, No Name"

}