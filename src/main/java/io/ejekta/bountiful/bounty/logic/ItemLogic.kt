package io.ejekta.bountiful.bounty.logic

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


class ItemLogic(override val entry: BountyDataEntry) : IEntryLogic {

    private fun getCurrentStacks(player: PlayerEntity): Map<ItemStack, Int> {
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

    override fun format(isObj: Boolean, progress: Progress): Text {
        val item = Registry.ITEM.get(Identifier(entry.content))
        return when (isObj) {
            true -> item.name.copy().formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(item.name.colored(entry.rarity.color))
        }
    }

    override fun getProgress(player: PlayerEntity): Progress {
        val needed = getCurrentStacks(player)
        return Progress(needed.values.sum(), entry.amount)
    }

    override fun finishObjective(player: PlayerEntity): Boolean {
        val needed = getCurrentStacks(player)
        return if (needed.values.sum() >= entry.amount) { // completed
            needed.forEach { (stack, toShrink) ->
                stack.decrement(toShrink)
            }
            true
        } else {
            false
        }
    }

    override fun giveReward(player: PlayerEntity): Boolean {
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