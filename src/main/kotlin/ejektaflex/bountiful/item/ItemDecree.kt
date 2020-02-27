package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.BountifulAPI
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.ext.hackyRandom
import ejektaflex.bountiful.api.ext.sendTranslation
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.logic.BountyChecker
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.data.BountyNBT
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.logic.checkers.CheckerRegistry
import ejektaflex.bountiful.logic.checkers.StackCheckHandler
import ejektaflex.bountiful.registry.DecreeRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Rarity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraft.util.text.TranslationTextComponent


class ItemDecree() : Item(
        Item.Properties().maxStackSize(1)
) {


    /**
     * Thrown when bounty NBT data could not be created
     */
    class DecreeCreationException(err: String = "Decree could not be created!") : Exception(err)

    var decreeId: String? = null

    val decree: Decree?
        get() = decreeId?.let { DecreeRegistry.getDecree(it) }

    override fun getTranslationKey() = "bountiful.decree"

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        return if (decree != null) {
            StringTextComponent(decree!!.decreeTitle)
        } else {
            TranslationTextComponent(getTranslationKey(stack))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        tooltip.add(StringTextComponent("Replace Me!"))
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {

        val held = playerIn.getHeldItem(handIn)
        ensureDecree(held)

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    private fun ensureDecree(stack: ItemStack) {
        val data = try {
            DecreeRegistry.content.hackyRandom()
        } catch (e: DecreeCreationException) {
            return
        }
        if (stack.item is ItemDecree) {
            if (!stack.hasTag()) {
                stack.tag = data.serializeNBT()
            }
        } else {
            throw Exception("${stack.displayName} is not an ItemDecree, so you cannot generate decree data for it!")
        }
    }

}