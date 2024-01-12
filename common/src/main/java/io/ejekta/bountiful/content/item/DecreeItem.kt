package io.ejekta.bountiful.content.item

import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class DecreeItem : Item(
    Settings().maxCount(1).fireproof()
) {

    override fun getTranslationKey() = "bountiful.decree"

    override fun getName(stack: ItemStack?): Text {
        return Text.translatable(translationKey).formatted(Formatting.DARK_PURPLE)
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        if (stack != null && world != null) {
            val data = DecreeData[stack].tooltipInfo(world)
            tooltip.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
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
            val dd = DecreeData(rank = ranked)
            spawnRank.populateFunc(dd, decIds)
            return ItemStack(BountifulContent.DECREE_ITEM).apply {
                DecreeData[this] = dd
            }
        }
    }

}