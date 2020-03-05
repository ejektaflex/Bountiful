package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

abstract class AbstractBountyEntryStackLike : BountyEntry(), IBountyObjective {

    override val calculatedWorth: Int
        get() = unitWorth * amount

    abstract val validStacks: List<ItemStack>

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return StringTextComponent("§f${progress.color}").appendSibling(
                formattedName
        ).appendText("§r §f${progress.stringNums}")
    }

    override fun toString(): String {
        return "BountyEntry ($type) [Item: $content, Amount: ${amount} [${amountRange}], Worth: $unitWorth, NBT: $nbtTag, Weight: $weight]"
    }



}
