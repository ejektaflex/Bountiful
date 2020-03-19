package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.structure.DecreeList
import ejektaflex.bountiful.ext.*
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraft.util.text.TranslationTextComponent


class ItemDecree() : Item(
        Item.Properties().maxStackSize(1).group(BountifulContent.BountifulGroup)
) {

    init {
        addPropertyOverride(ResourceLocation("bountiful", "decreestatus")) { stack, world, entity ->
            if (stack.hasTag() && stack.tag!!.getUnsortedList("ids").isNotEmpty()) {
                1f
            } else {
                0f
            }
        }
    }

    /**
     * Thrown when bounty NBT data could not be created
     */
    class DecreeCreationException(err: String = "Decree could not be created!") : Exception(err)

    override fun getTranslationKey() = "bountiful.decree"

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        return TranslationTextComponent(getTranslationKey()).applyTextStyle {
            it.color = TextFormatting.DARK_PURPLE
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {

        val ids = stack.tag?.getUnsortedList("ids")?.map {
            nbt -> nbt.getString("id")
        }

        if (ids != null) {
            if (stack.tag != null) {
                val components = ids.map {
                    TranslationTextComponent("bountiful.decree.${it}.name").applyTextStyle {
                        style -> style.color = TextFormatting.GOLD
                    }
                }.forEach {
                    tooltip.add(it)
                }

            } else {
                tooltip.add(TranslationTextComponent("bountiful.decree.invalid").appendSibling(
                        StringTextComponent(" ($ids)")
                ))
            }
        } else {
            tooltip.add(TranslationTextComponent("bountiful.decree.notset"))
        }

        // TODO Add debug tool when holding sneak, showing which pools are being used
        //tooltip.add(StringTextComponent("Replace Me!"))
    }

    fun setData(stack: ItemStack, list: DecreeList) {
        stack.tag = list.serializeNBT()
    }

    fun ensureDecree(stack: ItemStack, defaultData: Decree? = null) {

        if (stack.item is ItemDecree) {
            if (!stack.hasTag()) {

                val data = try {
                    listOf(
                            if (DecreeRegistry.ids.isNotEmpty()) {
                                defaultData ?: DecreeRegistry.content.hackyRandom()
                            } else {
                                Decree.INVALID
                            }
                    )
                } catch (e: DecreeCreationException) {
                    BountifulMod.logger.error(e.message)
                    return
                }

                stack.tag = CompoundNBT().apply {
                    setUnsortedList("ids", data.toSet())
                }

            } else {

                // If the ID on the Decree is invalid AND the registry is not empty, turn it into a new random decree
                val tids = stack.tag!!.getUnsortedListTyped("ids") { Decree() }


                if (!tids.all { it.id in DecreeRegistry.ids }) {
                    stack.tag = null
                    ensureDecree(stack)
                }

            }
        } else {
            throw Exception("${stack.displayName} is not an ItemDecree, so you cannot generate decree data for it!")
        }
    }

    companion object {

        fun getData(stack: ItemStack): DecreeList? {
            if (!stack.hasTag()) {
                return null
            }

            return stack.toData(::DecreeList)
        }

        fun makeStack(): ItemStack {
            val newDecree = ItemStack(BountifulContent.Items.DECREE)
            newDecree.edit<ItemDecree> { ensureDecree(it) }
            return newDecree
        }

        fun makeStack(decree: Decree): ItemStack {
            val newDecree = ItemStack(BountifulContent.Items.DECREE)
            newDecree.edit<ItemDecree> { ensureDecree(it, decree) }
            return newDecree
        }

        fun makeStack(decId: String): ItemStack? {
            val decree = DecreeRegistry.content.find { it.id == decId }
            return if (decree != null) makeStack(decree) else null
        }


    }

}