package ejektaflex.bountiful.api.data.entry

import ejektaflex.bountiful.api.data.entry.feature.IAmount
import ejektaflex.bountiful.api.data.entry.feature.IEntryFeature
import ejektaflex.bountiful.api.data.entry.feature.IKilledAmount
import ejektaflex.bountiful.api.ext.toItemStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT

class BountyEntryStack : BountyEntry<BountyEntryStack.StackBountyFeatures>() {

    override var type: String = BountyType.Stack.id

    override val calculatedWorth: Int
        get() = unitWorth * feature.amount

    override fun pick(): BountyEntry<StackBountyFeatures> {
        return cloned().apply {
            feature!!.amount = randCount
        }
    }

    override val feature = StackBountyFeatures()

    inner class StackBountyFeatures : IAmount {
        override var amount: Int = 0
    }

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }
            return stack
        }

    override val prettyContent: String
        get() = "§f${feature.amount}x §a${itemStack?.displayName}§r"

    override fun toString(): String {
        return "BountyEntry (Stack) [Item: $content, Amount: ${feature.amount}, Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }

}
