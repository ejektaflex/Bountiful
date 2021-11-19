package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.tag.ItemTags
import net.minecraft.tag.Tag
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min


object ItemTagLogic : IEntryLogic {

    private fun getTag(entry: BountyDataEntry) = ItemTags.getTagGroup().getTag(Identifier(entry.content))

    fun getItems(entry: BountyDataEntry) = getTag(entry)?.values() ?: listOf()

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
        if (getTag(entry) == null) {
            return LiteralText("* '${entry.content}' is not a valid tag!")
        }
        return null
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: PlayerEntity): Map<ItemStack, Int>? {
        val validItems = getItems(entry)
        return player.inventory.main.collect(entry.amount) {
            item in validItems
        }
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(entry, player)
        val title = if (entry.translation != null) TranslatableText(entry.translation) else LiteralText(entry.name ?: entry.content)
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