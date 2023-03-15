package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.ext.withSibling
import net.minecraft.world.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting

abstract class AbstractBountyEntryStackLike : BountyEntry(), IBountyObjective, IBountyReward {

    override val calculatedWorth: Int
        get() = unitWorth * amount

    abstract val validStacks: List<ItemStack>

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return formattedName.mergeStyle(progress.color).apply {
            siblings.add(StringTextComponent(" "))
            siblings.add(StringTextComponent(progress.stringNums).mergeStyle(TextFormatting.WHITE))
        }
    }

    override fun tooltipReward(): ITextComponent {
        return StringTextComponent(amount.toString() + "x ").mergeStyle(TextFormatting.WHITE).withSibling(
                formattedName.mergeStyle(TextFormatting.AQUA)
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Item: $content, Amount: ${amount} [${amountRange}], Worth: $unitWorth, NBT: $nbtTag, Weight: $weight]"
    }

}
