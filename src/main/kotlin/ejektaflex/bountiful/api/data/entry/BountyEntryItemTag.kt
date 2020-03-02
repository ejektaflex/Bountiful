package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.hackyRandom
import ejektaflex.bountiful.api.ext.toItemStack
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tags.ItemTags
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class BountyEntryItemTag : AbstractBountyEntryStackLike(), IBountyObjective, IBountyReward {

    @Expose
    override var type: String = BountyType.ItemTag.id


    override fun validate() {
        if (type == BountyType.ItemTag.id) {
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



    override val formattedName: ITextComponent
        get() = StringTextComponent(name ?: content)

    override fun tooltipReward(): ITextComponent {
        return StringTextComponent("§f${amount}§fx §b").appendSibling(
                formattedName
        )
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
