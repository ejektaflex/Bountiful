package ejektaflex.bountiful.api.logic.pickable

import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack

class PickedEntryStack(val genericPick: PickedEntry) : IPickedEntry by genericPick {



    val itemStack: ItemStack?
        get() = contentID.toItemStack

    override val content: Any? = itemStack

}