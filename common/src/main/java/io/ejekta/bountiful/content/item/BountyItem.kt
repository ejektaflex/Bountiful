package io.ejekta.bountiful.content.item

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.Player
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import java.util.*

class BountyItem : Item(
    Settings().maxCount(1).fireproof()
) {

    override fun getName(stack: ItemStack): Text {
        if (Kambridge.isOnServer()) {
            return Text.translatable("bountiful.bounty")
        }
        val info = BountyStack(stack).info
        var text = Text.translatable(info.rarity.name.lowercase()
            // Capitalizing
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } + " Bounty ").formatted(info.rarity.color)
        if (info.rarity == BountyRarity.LEGENDARY) {
            text = text.formatted(ChatFormatting.BOLD)
        }
        if (BountifulIO.configData.bounty.shouldHaveTimersAndExpire) {
            text = text.append(
                Component.literal("(")
                    .append(info.formattedTimeLeft(MinecraftClient.getInstance().world!!))
                    .append(Component.literal(")"))
                    .formatted(ChatFormatting.WHITE)
            )
        }
        return text
    }

    fun tryCashIn(player: Player, stack: ItemStack): Boolean {
        return BountyStack(stack).tryCashIn(player)
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>?,
        type: TooltipType
    ) {
        if (Kambridge.isOnServer()) {
            return
        }
        val tips = BountyStack(stack).genTooltip(Kambridge.isOnServer(), type)
        tooltip?.addAll(tips)
        super.appendTooltip(stack, context, tooltip, type)
    }

}