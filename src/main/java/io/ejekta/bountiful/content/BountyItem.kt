package io.ejekta.bountiful.content

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.clientWorld
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import java.util.*

class BountyItem : Item(
    Settings().maxCount(1).fireproof()
) {

    override fun getName(stack: ItemStack?): Text {
        return if (stack != null && clientWorld() != null) {
            val info = BountyInfo[stack]
            //val data = BountyData[stack]
            var text = Text.translatable(info.rarity.name.lowercase()
                // Capitalizing
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } + " Bounty ").formatted(info.rarity.color)
            if (info.rarity == BountyRarity.LEGENDARY) {
                text = text.formatted(Formatting.BOLD)
            }
            if (BountifulIO.configData.shouldBountiesHaveTimersAndExpire) {
                text = text.append(
                    Text.literal("(")
                        .append(info.formattedTimeLeft(clientWorld()!!))
                        .append(Text.literal(")"))
                        .formatted(Formatting.WHITE)
                )
            }
            return text
        } else {
            Text.literal("No Bounty Stack Given")
        }
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        if (stack != null && world != null) {
            val data = BountyInfo[stack].genTooltip(BountyData[stack])
            tooltip?.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

}