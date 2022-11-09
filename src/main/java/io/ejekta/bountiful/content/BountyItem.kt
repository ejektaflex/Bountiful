package io.ejekta.bountiful.content

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.clientWorld
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import java.util.*

class BountyItem : Item(
    FabricItemSettings()
        .maxCount(1)
        .fireproof()
        .group(ItemGroup.MISC)
) {

    override fun getName(stack: ItemStack?): Text {
        return if (stack != null && clientWorld() != null) {
            val data = BountyData[stack]
            var text = Text.translatable(data.rarity.name.lowercase()
                // Capitalizing
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } + " Bounty ").formatted(data.rarity.color)
            if (data.rarity == BountyRarity.LEGENDARY) {
                text = text.formatted(Formatting.BOLD)
            }
            if (BountifulIO.configData.shouldBountiesHaveTimersAndExpire) {
                text = text.append(
                    Text.literal("(")
                        .append(data.formattedTimeLeft(clientWorld()!!))
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
            val data = BountyInfo[stack].tooltip
            //val data = BountyData[stack].tooltipInfo()
            tooltip?.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    companion object {
        fun create(data: BountyData? = null): ItemStack {
            return ItemStack(BountifulContent.BOUNTY_ITEM).apply {
                val theData = data ?: BountyData()
                BountyData[this] = theData
                BountyInfo.cacheWithData(this, theData)
            }
        }
    }

}