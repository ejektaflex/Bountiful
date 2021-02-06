package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.util.clientWorld
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

class BountyItem : Item(
    FabricItemSettings()
        .maxCount(1)
        .fireproof()
        .group(ItemGroup.MISC)
) {

    override fun getName(stack: ItemStack?): Text {
        return if (stack != null && clientWorld() != null) {
            val data = BountyData[stack]
            var text = TranslatableText(data.rarity.name.toLowerCase().capitalize() + " Bounty ").formatted(data.rarity.color)
            if (data.rarity == BountyRarity.LEGENDARY) {
                text = text.formatted(Formatting.BOLD)
            }
            text = text.append(
                    LiteralText("(")
                        .append(data.formattedTimeLeft(clientWorld()!!))
                        .append(LiteralText(")"))
                        .formatted(Formatting.WHITE)
                )
            return text
        } else {
            LiteralText("No Bounty Stack Given")
        }
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        if (stack != null && world != null) {
            val data = BountyData[stack].tooltipInfo(world)
            tooltip?.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    companion object {
        fun create(data: BountyData? = null): ItemStack {
            return ItemStack(BountifulContent.BOUNTY_ITEM).apply {
                BountyData[this] = data ?: BountyData()
            }
        }
    }

}