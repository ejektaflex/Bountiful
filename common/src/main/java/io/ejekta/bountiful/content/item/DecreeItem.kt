package io.ejekta.bountiful.content.item

import io.ejekta.bountiful.components.DecreeStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class DecreeItem : Item(
    Properties().stacksTo(1).fireResistant()
) {

    override fun getDescriptionId() = "bountiful.decree"

    override fun getName(stack: ItemStack): Component {
        return Component.translatable(descriptionId).withStyle(ChatFormatting.DARK_PURPLE)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        pTooltipComponents: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        if (Kambridge.isOnServer()) {
            return
        }
        if (pStack != null) {
            val data = pStack[BountifulContent.DECREE_DATA]?.tooltipInfo(Minecraft.getInstance().level!!)
            pTooltipComponents.addAll(data ?: emptySet())
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag)
    }

    companion object {
        fun create(
            spawnCondition: DecreeSpawnCondition,
            ranked: Int = 1,
            spawnRank: DecreeSpawnRank = DecreeSpawnRank.CONSTANT
        ): ItemStack {
            val spawnableDecrees = BountifulContent.Decrees.filter(spawnCondition.spawnFunc).map { it.id }
            return create(spawnableDecrees, ranked, spawnRank)
        }

        fun createWithAllDecrees(): ItemStack {
            val decIds = BountifulContent.Decrees.map { it.id }
            return create(decIds, decIds.size)
        }

        fun create(
            decIds: List<String>,
            ranked: Int = 1,
            spawnRank: DecreeSpawnRank = DecreeSpawnRank.CONSTANT
        ): ItemStack {
            val stack = ItemStack(BountifulContent.DECREE_ITEM)
            spawnRank.populateFunc(DecreeStack(stack).apply { rank = ranked }, decIds)
            return stack
        }
    }

}