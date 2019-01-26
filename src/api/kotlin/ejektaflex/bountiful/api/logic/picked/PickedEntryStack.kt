package ejektaflex.bountiful.api.logic.picked

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack

class PickedEntryStack(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {

    override fun timeMult() = 1.0

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tagCompound = it }
            return stack
        }

    override val contentObj: Any?
        get() = itemStack

    override val prettyContent: String
        get() = "§f${amount}x §a${itemStack?.displayName}§r"

    override fun isValid(): Boolean {
        return contentObj != null
    }

    override fun toString(): String {
        return "PickedEntry (Stack) [Item: $content, Amount: $amount, NBT: $tag]"
    }


}