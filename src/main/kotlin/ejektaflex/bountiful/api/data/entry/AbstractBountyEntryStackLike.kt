package ejektaflex.bountiful.api.data.entry

import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.item.ItemStack
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


    override fun tooltipObjective(progress: BountyProgress): String {
        return "§f${progress.color}${formattedName}§r §f${progress.stringNums}"
    }


    override fun toString(): String {
        return "BountyEntry ($type) [Item: $content, Amount: ${amount}, Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }



}
