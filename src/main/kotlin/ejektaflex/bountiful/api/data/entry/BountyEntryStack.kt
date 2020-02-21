package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.data.entry.feature.IAmount
import ejektaflex.bountiful.api.data.entry.feature.IEntryFeature
import ejektaflex.bountiful.api.data.entry.feature.IKilledAmount
import ejektaflex.bountiful.api.ext.toItemStack
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import kotlin.math.ceil
import kotlin.math.max

class BountyEntryStack : BountyEntry(), IBountyObjective, IBountyReward {

    @Expose
    override var type: String = BountyType.Stack.id

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override fun pick(worth: Int?): BountyEntry {
        return cloned().apply {
            amount = if (worth != null) {
                max(1, ceil(worth.toDouble() / unitWorth).toInt())
            } else {
                randCount
            }
        }
    }


    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }
            return stack
        }

    override val prettyContent: String
        get() {
            return if (name != null) {
                "§f${amount}x §a$name§r"
            } else {
                "§f${amount}x §a${itemStack?.displayName!!.formattedText}§r"
            }
        }

    override fun toString(): String {
        return "BountyEntry (Stack) [Item: $content, Amount: ${amount}, Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }



}
