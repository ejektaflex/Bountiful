package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.hackyRandom
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tags.ItemTags
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

class BountyEntryItemTag : AbstractBountyEntryStackLike(), IBountyObjective, IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.ItemTag.id

    override val formattedName: ITextComponent
        get() = StringTextComponent(name ?: content)

    override fun validate() {
        if (bType == BountyType.ItemTag.id) {
            if (tagElements.isEmpty()) {
                throw EntryValidationException("Tag '$content' does not exist or was empty!")
            }
        }
        super.validate()
    }

    val tagElements: List<Item>
        get() = ItemTags.getCollection().getOrCreate(ResourceLocation(content)).allElements.toList()

    override val validStacks: List<ItemStack>
        get() {
            val tag = ItemTags.getCollection().getOrCreate(ResourceLocation(content))

            return tag.allElements.map { element ->
                element.defaultInstance.apply {
                    count = amount
                    this.tag?.let { t -> this.tag = t }
                }
            }
        }

    override fun reward(player: PlayerEntity) {
        var amountNeeded = amount
        val stacksToGive = mutableListOf<ItemStack>()

        while (amountNeeded > 0) {
            val randItem = validStacks.hackyRandom().copy()
            val stackSize = min(amountNeeded, randItem.maxStackSize)
            randItem.count = stackSize
            stacksToGive.add(randItem)
            amountNeeded -= stackSize
        }

        stacksToGive.forEach { stack ->
            ItemHandlerHelper.giveItemToPlayer(player, stack)
        }
    }

}
