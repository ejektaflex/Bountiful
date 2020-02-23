package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toItemStack
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.item.ItemStack
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

    override val prettyContent: String
        get() {

            return when (type) {
                "tag" -> "§f${amount}x §b${name ?: content}§r"
                "stack" -> "§f${amount}x §b${itemStack?.displayName!!.formattedText}§r"
                else -> "??? Stack?"
            }

        }

    val formattedName: String
        get() {
            return when (type) {
                "tag" -> name ?: content
                "stack" -> itemStack?.displayName!!.formattedText
                else -> "???"
            }
        }

    override fun tooltipView(progress: BountyProgress): String {
        return "§f${progress.stringNums} ${progress.color}${formattedName}§r"
    }

    override fun toString(): String {
        return "BountyEntry (Stack) [Item: $content, Amount: ${amount}, Worth: $unitWorth, NBT: $tag, Weight: $weight]"
    }



}
