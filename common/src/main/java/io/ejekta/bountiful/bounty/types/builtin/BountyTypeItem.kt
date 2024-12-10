package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.types.IBountyExchangeable
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.identifier
import net.minecraft.ChatFormatting
import net.minecraft.client.MinecraftClient
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.Player
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.text.MutableComponent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import kotlin.jvm.optionals.getOrNull


class BountyTypeItem : IBountyExchangeable {

    override val id: ResourceLocation = ResourceLocation.parse("item")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return if (entry.content.startsWith("#")) {
            getTagItems(server.registryAccess(), getTagItemKey(
                ResourceLocation.parse(entry.content.substringAfter("#"))
            )).isNotEmpty()
        } else {
            val id = getItem(ResourceLocation.parse(entry.content)).identifier
            id == ResourceLocation.parse(entry.content)
        }
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: Player): Map<ItemStack, Int> {
        return player.inventory.items.collect(entry.amount) {
            identifier.toString() == entry.content
        }
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): MutableComponent {
        val progress = getProgress(entry, player, current)
        val itemName = getItemName(entry)
        return when (isObj) {
            true -> itemName.withStyle(progress.color).append(progress.neededText.colored(ChatFormatting.WHITE))
            false -> progress.givingText.append(itemName.colored(entry.rarity.color))
        }
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return getItemStack(entry).getTooltipLines(Item.TooltipContext.EMPTY, player, TooltipFlag.NORMAL)
    }

    override fun getProgress(entry: BountyDataEntry, player: Player, current: Int): Progress {
        return Progress(getCurrentStacks(entry, player).values.sum(), entry.amount)
    }

    override fun getNewCurrent(entry: BountyDataEntry, player: Player, current: Int): Int {
        return getCurrentStacks(entry, player).values.sum()
    }

    override fun consumeObjectives(entry: BountyDataEntry, player: Player, current: Int): Boolean {
        val currStacks = getCurrentStacks(entry, player)
        if (currStacks.values.sum() >= entry.amount) {
            currStacks.forEach { (stack, toShrink) ->
                stack.shrink(toShrink)
            }
            return true
        }
        return false
    }

    override fun giveReward(entry: BountyDataEntry, player: Player) {
        val item = getItem(entry)
        val toGive = (0 until entry.amount).chunked(item.defaultMaxStackSize).map { it.size }

        for (amtToGive in toGive) {
            val stack = ItemStack(item, amtToGive).apply {
                // TODO give itemstack NBT rewards
                //nbt = entry.nbt
            }
            // Try give directly to player, otherwise drop at feet
            if (!player.addItem(stack)) {
                val pos = player.position()
                val stackEntity = ItemEntity(player.level(), pos.x, pos.y, pos.z, stack).apply {
                    setPickUpDelay(0)
                }
                player.level().addFreshEntity(stackEntity)
            }
        }
    }

    companion object {
        fun getItem(entry: BountyDataEntry): Item {
            return getItem(ResourceLocation.parse(entry.content))
        }

        fun getItem(id: ResourceLocation): Item {
            return BuiltInRegistries.ITEM.get(id)
        }

        fun getItemStack(entry: BountyDataEntry): ItemStack {
            val item = getItem(entry)
            return ItemStack(item).apply {
                // TODO give itemstack NBT rewards
                //entry.nbt?.let { this.nbt = it }
            }
        }

        fun getItemName(entry: BountyDataEntry): MutableComponent {
            val itemStack = getItemStack(entry)
            val named = itemStack.displayName.copy()

            // TODO reimplement
            // Show enchanted book enchantments
//            if (itemStack.item is EnchantedBookItem && Kambridge.isOnClient()) {
//                val enchants = EnchantmentHelper.get(itemStack).toList()
//
//                if (enchants.isNotEmpty()) {
//                    named = named.append(" (")
//                    for ((enchant, level) in enchants.dropLast(1)) {
//                        named = named.append(enchant.getName(level)).append(", ")
//                    }
//                    named = named.append(enchants.last().run { first.getName(second) }).append(")")
//                }
//            }

            return named
        }
    }

}