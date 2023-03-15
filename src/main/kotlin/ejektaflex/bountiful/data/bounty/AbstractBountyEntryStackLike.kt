package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.ext.withSibling
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.util.text.StringTextComponent
import net.minecraft.ChatFormatting

abstract class AbstractBountyEntryStackLike : BountyEntry(), IBountyObjective, IBountyReward {

    override val calculatedWorth: Int
        get() = unitWorth * amount

    abstract val validStacks: List<ItemStack>

    override fun tooltipObjective(progress: BountyProgress): Component {
        return formattedName.withStyle(progress.color).apply {
            siblings.add(Component.literal(" "))
            siblings.add(Component.literal(progress.stringNums).withStyle(ChatFormatting.WHITE))
        }
    }

    override fun tooltipReward(): Component {
        return Component.literal(amount.toString() + "x ").withStyle(ChatFormatting.WHITE).withSibling(
                formattedName.withStyle(ChatFormatting.AQUA)
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Item: $content, Amount: ${amount} [${amountRange}], Worth: $unitWorth, NBT: $nbtTag, Weight: $weight]"
    }

}
