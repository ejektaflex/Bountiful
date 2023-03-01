package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil
import net.fabricmc.fabric.mixin.resource.conditions.TagManagerLoaderMixin
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World


object ItemTagLogic : IEntryLogic {

    private fun getTag(entry: BountyDataEntry) = TagKey.of(Registries.ITEM.key, Identifier(entry.content))


    fun getItems(world: World, entry: BountyDataEntry): List<Item> {
        return getTagItems(world, getTag(entry))
    }

    fun entryAppliesToStack(entry: BountyDataEntry, stack: ItemStack): Boolean {
        return stack.isIn(TagKey.of(Registries.ITEM.key, Identifier(entry.content)))
    }

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
//        if (getTag(entry) == null) {
//            return Text.literal("* '${entry.content}' is not a valid tag!")
//        }
        return null
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: PlayerEntity): Map<ItemStack, Int>? {
        return player.inventory.main.collect(entry.amount) {
            entryAppliesToStack(entry, this)
        }
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(entry, player)
        val title = if (entry.translation != null) Text.translatable(entry.translation) else Text.literal(entry.name ?: entry.content)
        return when (isObj) {
            true -> title.copy().formatted(progress.color).append(progress.neededText.colored(Formatting.WHITE))
            false -> progress.givingText.append(title.colored(entry.rarity.color))
        }
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(
            if (entry.translation != null) {
                textTranslate(entry.translation!!)
            } else {
                textLiteral("Item Tag")
            },
            textLiteral(entry.content) {
                format(Formatting.DARK_GRAY)
            }
        )
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(getCurrentStacks(entry, player)?.values?.sum() ?: 0, entry.amount)
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return getCurrentStacks(entry, player)?.let {
            it.forEach { (stack, toShrink) ->
                stack.decrement(toShrink)
            }
            true
        } ?: false
    }

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity) = false

}