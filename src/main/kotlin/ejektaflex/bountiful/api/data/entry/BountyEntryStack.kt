package ejektaflex.bountiful.api.data.entry

import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT

class BountyEntryStack : BountyEntry() {

    override var type: String = BountyType.Stack.id

    override fun deserializeNBT(tag: CompoundNBT) {
        super.deserializeNBT(tag)
        amount = tag.getInt("amount")
    }

    override fun serializeNBT(): CompoundNBT {
        return super.serializeNBT().apply {
            putInt("amount", amount)
        }
    }

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
