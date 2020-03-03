package ejektaflex.bountiful.api.data.entry

import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import kotlin.math.ceil
import kotlin.math.max

abstract class AbstractBountyEntryStackLike : BountyEntry(), IBountyObjective {

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

    abstract val validStacks: List<ItemStack>

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return StringTextComponent("§f${progress.color}").appendSibling(
                formattedName
        ).appendText("§r §f${progress.stringNums}")
    }

    override fun toString(): String {
        return "BountyEntry ($type) [Item: $content, Amount: ${amount} [${amountRange}], Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }



}
