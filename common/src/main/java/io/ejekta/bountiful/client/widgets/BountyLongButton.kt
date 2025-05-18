package io.ejekta.bountiful.client.widgets

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeEntity
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItemTag
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.messages.SelectBounty
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class BountyLongButton(val parent: BoardScreen, var bountyIndex: Int) : KWidget {

    override val width: Int = ButtonWidth
    override val height: Int = ButtonHeight

    fun getStack(): ItemStack {
        return parent.boardHandler.container.getItem(bountyIndex)
    }

    private val reactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            parent.boardHandler.container.select(bountyIndex)
            SelectBounty(bountyIndex, Minecraft.getInstance().player!!.stringUUID).sendToServer()
        }
    }

    private fun renderEntryBasedOnLogic(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int, isReward: Boolean) {
        when (entry.logic.id) {
            BountyTypeRegistry.COMMAND.id -> {
                dsl { itemStackIcon(ItemStack(Items.COMMAND_BLOCK), x, y + 1) }
            }
            BountyTypeRegistry.ITEM.id -> {
                val stack = BountyTypeItem.getItemStack(entry, Minecraft.getInstance().level!!.registryAccess()).apply {
                    count = entry.amount
                }
                dsl { itemStackIcon(stack, x, y) }
            }
            BountyTypeRegistry.ITEM_TAG.id -> {
                val world = Minecraft.getInstance().level ?: return
                val frameTime = (world.gameTime / 30L).toInt()
                val options = BountyTypeItemTag.getItems(world, entry).map { ItemStack(it) }.takeUnless { it.isEmpty() } ?: return
                val frame = frameTime % options.size
                dsl {
                    itemStackIcon(options[frame], x, y)
                }
            }
            BountyTypeRegistry.ENTITY.id -> {
                val entityType = BountyTypeEntity.getEntityType(entry)

                // We need to limit to living entities if at all possible, just a sanity check
                if (entityType.category != MobCategory.CREATURE && entityType.category != MobCategory.MONSTER) {
                    return
                }

                dsl.area(x, y, 16, 16) {
                    livingEntity(entityType as? EntityType<out LivingEntity>
                        ?: throw Exception("Bounty cannot have ${entry.content} as entity objective, it is not a LivingEntity!"),
                        size = 14.0
                    )
                }
            }
            BountyTypeRegistry.CRITERIA.id -> {
                dsl { itemStackIcon(ItemStack(Items.PAINTING), x, y) }
            }
            else -> {}
        }
    }

    private fun renderEntry(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int, isReward: Boolean = false) {

        if (entry.icon != null) {
            val itemForIcon = BuiltInRegistries.ITEM.get(entry.icon)
            dsl { itemStackIcon(ItemStack(itemForIcon), x, y) }
        } else {
            renderEntryBasedOnLogic(dsl, entry, x, y, isReward)
        }

        // Render amount
        dsl {
            val textToShow = textLiteral(entry.amount.toString()) {
                color = if (isReward) {
                    entry.rarity.color.color ?: 0xFFFFFF
                } else {
                    0xFFFFFF
                }
            }
            val tr = Minecraft.getInstance().font
            context.pose().pushPose()
            context.pose().translate(0f, 0f, 200f)
            context.drawString(
                tr,
                textToShow,
                (ctx.absX(x) + 18 - tr.width(textToShow)),
                ctx.absY(y) + 10,
                0xFFFFFF,
                true
            )
            context.pose().popPose()
        }
        // Entry tooltip
        dsl {
            onHover(x, y, 18, 18) {
                tooltip(entry.textOnBoardSidebar(Minecraft.getInstance().player!!))
            }
        }
    }

    private fun isSelected(): Boolean {
        return ItemStack.matches(parent.boardHandler.container.getItem(-1), parent.boardHandler.container.getItem(bountyIndex))
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area.reactWith(reactor)
        area.dsl {
            // Draw button background
            img(BUTTON, width, height)

            area(width, height) {
                if (isSelected()) {
                    rect(0x41261b, 0x96)
                } else {
                    rect(0xb86f50, 0x48)
                }
                onHover {
                    if (!isSelected()) {
                        rect(0xFFFFFF, 0x33)
                    }
                }
            }



            // Render objectives
            renderEntries(getStack()[BountifulContent.BOUNTY_OBJS]!!) { rx, ry, e ->
                renderEntry(this, e, rx, ry, false)
            }

            offset(width / 2 - 10, 0) {
                img(ARROW, 20, 20)
            }

            // Render rewards
            renderEntries(getStack()[BountifulContent.BOUNTY_REWS]!!) { rx, ry, e ->
                renderEntry(this, e, BountyZoneSize + ArrowWidth + rx, ry, true)
            }
        }
    }

    companion object {
        val BUTTON = ResourceLocation.parse("widget/button")
        val ARROW = Bountiful.id("arrow")

        const val ButtonWidth = 160
        const val ButtonHeight = 20
        const val ArrowWidth = 20
        const val BountyZoneSize = (ButtonWidth - ArrowWidth) / 2

        fun KGuiDsl.renderEntries(entries: List<BountyDataEntry>, renderFunc: KGuiDsl.(rx: Int, ry: Int, e: BountyDataEntry) -> Unit) {
            for (i in entries.indices) {
                val spaceDiff = BountyZoneSize - (entries.size * 18)
                val spaceStart = spaceDiff / 2
                renderFunc(
                    (spaceStart + (i * 18)), 1, entries[i]
                )
            }
        }

    }

}