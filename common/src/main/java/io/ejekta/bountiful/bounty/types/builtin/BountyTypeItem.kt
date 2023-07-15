package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.IBountyExchangeable
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.identifier
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier


class BountyTypeItem : IBountyExchangeable {

    override val id: Identifier = Identifier("item")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return if (entry.content.startsWith("#")) {
            getTagItems(server.registryManager, getTagItemKey(
                Identifier(entry.content.substringAfter("#"))
            )).isNotEmpty()
        } else {
            val id = getItem(Identifier(entry.content)).identifier
            id == Identifier(entry.content)
        }
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: PlayerEntity): Map<ItemStack, Int> {
        return player.inventory.main.collect(entry.amount) {
            identifier.toString() == entry.content
        }
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        val progress = getProgress(entry, player)
        val itemName = getItemName(entry)
        return when (isObj) {
            true -> itemName.formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(itemName.colored(entry.rarity.color))
        }
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return getItemStack(entry).getTooltip(player, TooltipContext.BASIC)
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(getCurrentStacks(entry, player).values.sum(), entry.amount)
    }

    override fun getNewCurrent(entry: BountyDataEntry, player: PlayerEntity): Int {
        return getCurrentStacks(entry, player).values.sum()
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val currStacks = getCurrentStacks(entry, player)
        if (currStacks.values.sum() >= entry.amount) {
            currStacks.forEach { (stack, toShrink) ->
                stack.decrement(toShrink)
            }
            return true
        }
        return false
    }

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val item = getItem(entry)
        val toGive = (0 until entry.amount).chunked(item.maxCount).map { it.size }

        for (amtToGive in toGive) {
            val stack = ItemStack(item, amtToGive).apply {
                nbt = entry.nbt
            }
            // Try give directly to player, otherwise drop at feet
            if (!player.giveItemStack(stack)) {
                val stackEntity = ItemEntity(player.world, player.pos.x, player.pos.y, player.pos.z, stack).apply {
                    setPickupDelay(0)
                }
                player.world.spawnEntity(stackEntity)
            }
        }

        return true
    }

    companion object {
        fun getItem(entry: BountyDataEntry): Item {
            return getItem(Identifier(entry.content))
        }

        fun getItem(id: Identifier): Item {
            return Registries.ITEM.get(id)
        }

        fun getItemStack(entry: BountyDataEntry): ItemStack {
            val item = getItem(entry)
            return ItemStack(item).apply {
                entry.nbt?.let { this.nbt = it }
            }
        }

        fun getItemName(entry: BountyDataEntry): MutableText {
            return getItem(entry).name.copy()
        }
    }

}