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

class BountyEntryStack : AbstractBountyEntryStackLike(), IBountyObjective, IBountyReward {

    @Expose
    override var type: String = BountyType.Stack.id

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }
            return stack
        }

    override fun validate() {
        val stackie = itemStack
        if (stackie?.item == Items.AIR) {
            throw EntryValidationException("Stack '$content' does not exist!")
        }
        super.validate()
    }

    override val validStacks: List<ItemStack>
        get() {
            val stack = content.toItemStack
            tag?.let { stack?.tag = it }

            return listOfNotNull(stack)
        }

    override val formattedName: String
        get() = itemStack?.displayName!!.formattedText


    override fun tooltipReward(): String {
        return "§f${amount}§fx §b$formattedName"
    }

}
