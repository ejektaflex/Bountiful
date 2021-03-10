package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min


object ItemLogic : IEntryLogic {

    override fun format(entry: BountyDataEntry, isObj: Boolean, progress: Pair<Int, Int>): Text {
        val item = Registry.ITEM.get(Identifier(entry.content))
        return when (isObj) {
            true -> item.name.colored(progress).append(progress.needed.colored(Formatting.WHITE))
            false -> progress.giving.append(item.name.colored(entry.rarity.color))
        }
    }

    private fun getNeeded(data: BountyData, entry: BountyDataEntry, player: PlayerEntity): Map<ItemStack, Int> {
        val selected = mutableMapOf<ItemStack, Int>()
        var needed = entry.amount
        for (stack in player.inventory.main) {
            if (stack.identifier.toString() == entry.content) {
                val num = selected.getOrPut(stack) { 0 }

                val weNeed = min(needed, stack.count)
                selected[stack] = num + weNeed
                needed -= weNeed

                if (needed == 0) {
                    return selected
                }
            }
        }
        return selected
    }


    override fun getProgress(data: BountyData, entry: BountyDataEntry, player: PlayerEntity): Pair<Int, Int> {
        val needed = getNeeded(data, entry, player)
        return needed.values.sum() to entry.amount
    }

    override fun finishObjective(data: BountyData, entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val needed = getNeeded(data, entry, player)
        return if (needed.values.sum() >= entry.amount) { // completed
            needed.forEach { (stack, toShrink) ->
                stack.decrement(toShrink)
            }
            true
        } else {
            false
        }
    }

    override fun giveReward(data: BountyData, entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val item = Registry.ITEM[Identifier(entry.content)]
        val toGive = (0 until entry.amount).chunked(item.maxCount).map { it.size }

        for (amtToGive in toGive) {
            val stack = ItemStack(item, amtToGive).apply {
                tag = entry.nbtData as CompoundTag?
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

}