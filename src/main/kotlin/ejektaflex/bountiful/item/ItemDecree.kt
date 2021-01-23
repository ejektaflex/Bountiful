package ejektaflex.bountiful.item

import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.data.structure.DecreeList
import ejektaflex.bountiful.ext.*
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.*
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * A Decree [Item] in game.
 *
 * @constructor Creates a new Decree item.
 */
class ItemDecree : Item(
        Properties().maxStackSize(1).group(BountifulContent.BountifulGroup)
) {

    /**
     * Thrown when bounty NBT data could not be created
     */
    class DecreeCreationException(err: String = "Decree could not be created!") : Exception(err)

    override fun getTranslationKey() = "bountiful.decree"

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        return TranslationTextComponent(translationKey).modStyle {
            color = Color.fromTextFormatting(TextFormatting.DARK_PURPLE)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {

        val ids = stack.tag?.getUnsortedList("ids")?.map { nbt ->
            nbt.getString("id")
        }

        if (ids != null) {
            if (stack.tag != null) {
                val components = ids.map {
                    TranslationTextComponent("bountiful.decree.${it}.name").modStyle {
                        color = Color.fromTextFormatting(TextFormatting.GOLD)
                    }
                }.forEach {
                    tooltip.add(it)
                }

            } else {
                tooltip.add(TranslationTextComponent("bountiful.decree.invalid").append(
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

        /**
         * Attempts to combine two Decree items into a new one with both Decree IDs.
         *
         * @return The newly created Decree itemstack
         */
        fun combine(stackA: ItemStack, stackB: ItemStack): ItemStack? {

            if (stackA.item is ItemDecree && stackB.item is ItemDecree && stackA.hasTag() && stackB.hasTag()) {
                val idsA = stackA.toData(::DecreeList)
                val idsB = stackB.toData(::DecreeList)
                val totals = idsA + idsB
                val out = makeStack()

                out.edit<ItemDecree> {
                    setData(it, totals)
                }

                return out
            }
            return null
        }

        /**
         * Creates a new Decree [ItemStack] and assigns it a random valid Decree.
         *
         * @return The new Decree [ItemStack]
         */
        fun makeStack(): ItemStack {
            val newDecree = ItemStack(BountifulContent.DECREE)
            newDecree.edit<ItemDecree> { ensureDecree(it) }
            return newDecree
        }

        /**
         * Creates a new Decree [ItemStack] with specific Decree data.
         *
         * @return The new Decree [ItemStack]
         */
        fun makeStack(decree: Decree): ItemStack {
            val newDecree = ItemStack(BountifulContent.DECREE)
            newDecree.edit<ItemDecree> { ensureDecree(it, decree) }
            return newDecree
        }

        /**
         * Attempts to create a new Decree [ItemStack] with the given Decree ID,
         * or returns null if no Decree exists with that ID.
         *
         * @return The new Decree [ItemStack]
         */
        fun makeStack(decId: String): ItemStack? {
            val decree = DecreeRegistry.content.find { it.id == decId }
            return if (decree != null) makeStack(decree) else null
        }

        /**
         * Creates a new Decree [ItemStack] with a random Decree for data,
         * or null if the Decree Registry is empty.
         */
        fun makeRandomStack(): ItemStack? {
            return if (DecreeRegistry.ids.isNotEmpty()) {
                makeStack(DecreeRegistry.content.hackyRandom())
            } else {
                null
            }
        }


    }

}