package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.toItemStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

class BountyEntryItem : AbstractBountyEntryStackLike(), IBountyObjective, IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Item.id

    override val formattedName: ITextComponent
        get() = StringTextComponent(itemStack?.displayName!!.formattedText)

    val itemStack: ItemStack?
        get() {
            val stack = content.toItemStack
            nbtTag?.let { stack?.tag = it }
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
            nbtTag?.let { stack?.tag = it }

            return listOfNotNull(stack)
        }

    override fun reward(player: PlayerEntity) {
        var amountNeeded = amount
        val stacksToGive = mutableListOf<ItemStack>()

        while (amountNeeded > 0) {
            val stackSize = min(amountNeeded, itemStack!!.maxStackSize)
            val newStack = itemStack!!.copy().apply {
                count = stackSize
            }
            stacksToGive.add(newStack)
            amountNeeded -= stackSize
        }

        stacksToGive.forEach { stack ->
            ItemHandlerHelper.giveItemToPlayer(player, stack)
        }
    }

}
