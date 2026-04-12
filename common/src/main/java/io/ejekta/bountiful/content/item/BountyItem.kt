package io.ejekta.bountiful.content.item

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import java.util.*
import java.util.function.Consumer

class BountyItem(props: Properties) : Item(
    props.stacksTo(1).fireResistant()
) {

    override fun getName(stack: ItemStack): Component {
        if (Kambridge.isOnServer()) {
            return Component.translatable("bountiful.bounty")
        }
        val clientLevel = Minecraft.getInstance().level
        val info = BountyStack(stack).info
        var text = Component.translatable(info.rarity.name.lowercase()
            // Capitalizing
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } + " Bounty ").withStyle(info.rarity.color)
        if (info.rarity == BountyRarity.LEGENDARY) {
            text = text.withStyle(ChatFormatting.BOLD)
        }
        if (BountifulIO.configData.bounty.shouldHaveTimersAndExpire && clientLevel != null) {
            text = text.append(
                Component.literal("(")
                    .append(info.formattedTimeLeft(clientLevel))
                    .append(Component.literal(")"))
                    .withStyle(ChatFormatting.WHITE)
            )
        }
        return text
    }

    fun tryCashIn(player: Player, stack: ItemStack): Boolean {
        return BountyStack(stack).tryCashIn(player)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResult {
        val hitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE)
        if (!level.isClientSide && hitResult.type == HitResult.Type.MISS) {
            player.sendOverlayMessage(Component.translatable("bountiful.bounty.turnin"))
        }
        return InteractionResult.PASS
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        if (!level.isClientSide && !level.getBlockState(context.clickedPos).`is`(BountifulContent.BOARD.value)) {
            context.player?.sendOverlayMessage(Component.translatable("bountiful.bounty.turnin"))
        }
        return InteractionResult.PASS
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: Item.TooltipContext,
        pDisplay: TooltipDisplay,
        pTooltip: Consumer<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        if (Kambridge.isOnServer()) {
            return
        }
        val tips = BountyStack(pStack).genTooltip(Kambridge.isOnServer(), pTooltipFlag)
        tips.forEach(pTooltip)
        super.appendHoverText(pStack, pContext, pDisplay, pTooltip, pTooltipFlag)
    }

}
