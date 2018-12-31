package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack

class PickedEntryStack(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {

    override val timeMult = 1.0

    val itemStack: ItemStack?
        get() = content.toItemStack

    override val contentObj: Any?
        get() = itemStack

    override val prettyContent: String
        get() = "§f${amount}x §a${itemStack?.displayName}§r"

}