package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min


class ItemLogic(override val entry: BountyDataEntry) : IEntryLogic {

    private val item: Item
        get() = Registry.ITEM.get(Identifier(entry.content))

    private val itemName: MutableText
        get() = ItemStack(item).apply {
            entry.nbt?.let { this.nbt = it }
        }.name.copy()

    override fun verifyValidity(player: PlayerEntity): MutableText? {
        val id = item.identifier
        if (id != Identifier(entry.content)) {
            return LiteralText("* '${entry.content}' is not a valid item!")
        }
        return null
    }

    private fun getCurrentStacks(player: PlayerEntity): Map<ItemStack, Int>? {
        return player.inventory.main.collect(entry.amount) {
            identifier.toString() == entry.content
        }
    }

    override fun format(isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(player)
        return when (isObj) {
            true -> itemName.formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(itemName.colored(entry.rarity.color))
        }
    }

    override fun getProgress(player: PlayerEntity): Progress {
        return Progress(getCurrentStacks(player)?.values?.sum() ?: 0, entry.amount)
    }

    override fun tryFinishObjective(player: PlayerEntity): Boolean {
        return getCurrentStacks(player)?.let {
            it.forEach { (stack, toShrink) ->
                stack.decrement(toShrink)
            }
            true
        } ?: false
    }

    override fun giveReward(player: PlayerEntity): Boolean {
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

}