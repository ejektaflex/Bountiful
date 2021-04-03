package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.tag.ItemTags
import net.minecraft.tag.Tag
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min


class ItemTagLogic(override val entry: BountyDataEntry) : IEntryLogic {

    private fun getTag() = ItemTags.getTagGroup().getTag(Identifier(entry.content))

    private fun getItems() = getTag()?.values() ?: listOf()

    override fun verifyValidity(player: PlayerEntity): MutableText? {
        if (getTag() == null) {
            return LiteralText("* '${entry.content}' is not a valid tag!")
        }
        return null
    }

    private fun getCurrentStacks(player: PlayerEntity): Map<ItemStack, Int>? {
        val validItems = getItems()
        return player.inventory.main.collect(entry.amount) {
            item in validItems
        }
    }

    override fun format(isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(player)
        val title = LiteralText(entry.name ?: entry.content)
        return when (isObj) {
            true -> title.copy().formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(title.colored(entry.rarity.color))
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
        /*
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

         */
        return false
    }

}