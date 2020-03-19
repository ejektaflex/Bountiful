package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting

abstract class AbstractBountyEntryStackLike : BountyEntry(), IBountyObjective, IBountyReward {

    override val calculatedWorth: Int
        get() = unitWorth * amount

    abstract val validStacks: List<ItemStack>

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return formattedName.applyTextStyle {
            it.color = progress.color
        }.appendSibling(
                StringTextComponent(" ")
        ).appendSibling(
                StringTextComponent(progress.stringNums).applyTextStyle {
                    it.color = TextFormatting.WHITE
                }
        )
    }

    override fun tooltipReward(): ITextComponent {
        return StringTextComponent(amount.toString() + "x ").applyTextStyle {
            it.color = TextFormatting.WHITE
        }.appendSibling(
                formattedName.applyTextStyle {
                    it.color = TextFormatting.AQUA
                }
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Item: $content, Amount: ${amount} [${amountRange}], Worth: $unitWorth, NBT: $nbtTag, Weight: $weight]"
    }

}
