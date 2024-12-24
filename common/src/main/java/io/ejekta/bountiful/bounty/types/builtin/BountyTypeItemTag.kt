package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.collect
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class BountyTypeItemTag : IBountyObjective {

    override val id: ResourceLocation = ResourceLocation.parse("item_tag")

    private fun entryAppliesToStack(entry: BountyDataEntry, stack: ItemStack): Boolean {
        return stack.`is`(TagKey.create(Registries.ITEM, ResourceLocation.parse(entry.content)))
    }

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return getTagItems(server.registries().compositeAccess(), getTagItemKey(ResourceLocation.parse(entry.content))).isNotEmpty()
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: Player): Map<ItemStack, Int>? {
        return player.inventory.items.collect(entry.amount) {
            entryAppliesToStack(entry, this)
        }
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): MutableComponent {
        val progress = getProgress(entry, player, current)
        val title = if (entry.name != null) Component.literal(entry.name) else entry.translation
        return when (isObj) {
            true -> title.copy().withStyle(progress.color).append(progress.neededText.colored(ChatFormatting.WHITE))
            false -> progress.givingText.append(title.colored(entry.rarity.color))
        }
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return listOf(
            if (entry.name != null) {
                Component.literal(entry.name)
            } else {
                entry.translation
            },
            Component.literal(entry.content).withStyle(ChatFormatting.DARK_GRAY)
        )
    }

    override fun getProgress(entry: BountyDataEntry, player: Player, current: Int): Progress {
        return Progress(getCurrentStacks(entry, player)?.values?.sum() ?: 0, entry.amount)
    }

    override fun getNewCurrent(entry: BountyDataEntry, player: Player, current: Int): Int {
        return getCurrentStacks(entry, player)?.values?.sum() ?: 0
    }

    override fun consumeObjectives(entry: BountyDataEntry, player: Player, current: Int): Boolean {
        return getCurrentStacks(entry, player)?.let {
            it.forEach { (stack, toShrink) ->
                stack.shrink(toShrink)
            }
            true
        } ?: false
    }

    companion object {
        private fun getTag(entry: BountyDataEntry) = TagKey.create(Registries.ITEM, ResourceLocation.parse(entry.content))

        fun getItems(world: Level, entry: BountyDataEntry): List<Item> {
            return getTagItems(world.registryAccess(), getTag(entry))
        }
    }

}