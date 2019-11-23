package ejektaflex.bountiful.api.data.entry

import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack

class BountyEntryStack : BountyEntry() {

    override val type: String = BountyType.Stack.id

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }
            return stack
        }

    override val contentObj: ItemStack?
        get() = itemStack

    override val prettyContent: String
        get() = "§f${amount}x §a${itemStack?.displayName}§r"

    override fun toString(): String {
        return "BountyEntry (Stack) [Item: $content, Amount: $amount, Worth: $unitWorth, NBT: $tag, Weight: $weight, Stages: $stages]"
    }

}
