package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toItemStack
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tags.ItemTags
import net.minecraft.util.ResourceLocation
import kotlin.math.ceil
import kotlin.math.max

class BountyEntryStack : BountyEntry(), IBountyObjective, IBountyReward {

    @Expose
    override var type: String = BountyType.Stack.ids.first()

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override fun pick(worth: Int?): BountyEntry {
        return cloned().apply {
            amount = if (worth != null) {
                max(1, ceil(worth.toDouble() / unitWorth).toInt())
            } else {
                randCount
            }
        }
    }

    override fun validate() {
        if (type == "tag") {
            if (tagElements.isEmpty()) {
                throw EntryValidationException("Tag '$content' does not exist!")
            }
        } else if (type == "stack") {
            val stackie = itemStack
            if (stackie?.item == Items.AIR) {
                throw EntryValidationException("Stack '$content' does not exist!")
            }
        }
        if (amountRange.min < 1) {
            throw EntryValidationException("'$content' cannot have an amount possibly less than 1!")
        }
        if (amountRange.min > amountRange.max) {
            throw EntryValidationException("'$content' cannot have a min amount greater than it's max!")
        }
        return true
    }

    val tagElements: List<Item>
        get() = ItemTags.getCollection().getOrCreate(ResourceLocation(content)).allElements.toList()

    val validStacks: List<ItemStack>
        get() {
            return if (type == "stack") {

                val stack = content.toItemStack
                tag?.let { stack?.tag = it }

                listOfNotNull(stack)

            } else {

                val tag = ItemTags.getCollection().getOrCreate(ResourceLocation(content))

                tag.allElements.map { element ->
                    element.defaultInstance.apply {
                        count = amount
                        this.tag?.let { t -> this.tag = t }
                    }
                }

            }
        }

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }
            return stack
        }

    override val formattedName: String
        get() {
            return when (type) {
                "tag" -> name ?: content
                "stack" -> itemStack?.displayName!!.formattedText
                else -> "???"
            }
        }

    override fun tooltipObjective(progress: BountyProgress): String {
        return "§f${progress.color}${formattedName}§r §f${progress.stringNums}"
    }

    override fun tooltipReward(): String {
        return "§f${amount}§fx §b$formattedName"
    }

    override fun toString(): String {
        return "BountyEntry (Stack) [Item: $content, Amount: ${amount}, Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }



}
