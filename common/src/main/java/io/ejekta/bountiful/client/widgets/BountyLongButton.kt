package io.ejekta.bountiful.client.widgets

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeEntity
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItemTag
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.messages.SelectBounty
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class BountyLongButton(val parent: BoardScreen, var bountyIndex: Int) : KWidget {

    override val width: Int = ButtonWidth
    override val height: Int = ButtonHeight

    fun getBountyData(): BountyData {
        return BountyData[parent.boardHandler.inventory.getStack(bountyIndex)]
    }

    private val reactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            parent.boardHandler.inventory.select(bountyIndex)
            SelectBounty(bountyIndex, MinecraftClient.getInstance().player!!.uuidAsString).sendToServer()
        }
    }

    private fun renderEntryBasedOnLogic(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int, isReward: Boolean) {
        when (entry.logicId) {
            BountyTypeRegistry.COMMAND.id -> {
                dsl { itemStackIcon(ItemStack(Items.COMMAND_BLOCK), x, y + 1) }
            }
            BountyTypeRegistry.ITEM.id -> {
                val stack = BountyTypeItem.getItemStack(entry).apply {
                    count = entry.amount
                }
                dsl { itemStackIcon(stack, x, y) }
            }
            BountyTypeRegistry.ITEM_TAG.id -> {
                val world = MinecraftClient.getInstance().world ?: return
                val frameTime = (world.time / 30L).toInt()
                val options = BountyTypeItemTag.getItems(world, entry).map { ItemStack(it) }.takeUnless { it.isEmpty() } ?: return
                val frame = frameTime % options.size
                dsl {
                    itemStackIcon(options[frame], x, y)
                }
            }
            BountyTypeRegistry.ENTITY.id -> {
                val entityType = BountyTypeEntity.getEntityType(entry)

                if (entityType.spawnGroup != SpawnGroup.CREATURE && entityType.spawnGroup != SpawnGroup.MONSTER) {
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
            val itemForIcon = Registries.ITEM.get(entry.icon)
            dsl { itemStackIcon(ItemStack(itemForIcon), x, y) }
        } else {
            renderEntryBasedOnLogic(dsl, entry, x, y, isReward)
        }

        // Render amount
        dsl {
            val textToShow = textLiteral(entry.amount.toString()) {
                color = if (isReward) {
                    entry.rarity.color.colorValue ?: 0xFFFFFF
                } else {
                    0xFFFFFF
                }
            }
            val tr = MinecraftClient.getInstance().textRenderer
            textImmediate(x + 17 - tr.getWidth(textToShow.string) * 2, y + 9, textToShow)
        }
        // Entry tooltip
        dsl {
            onHover(x, y, 18, 18) {
                tooltip(entry.textBoard(MinecraftClient.getInstance().player!!))
            }
        }
    }

    private fun isSelected(): Boolean {
        return ItemStack.areEqual(parent.boardHandler.inventory.getStack(-1), parent.boardHandler.inventory.getStack(bountyIndex))
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

            val data = getBountyData()

            // Render objectives
            renderEntries(data.objectives) { rx, ry, e ->
                renderEntry(this, e, rx, ry, false)
            }

            offset(width / 2 - 10, 0) {
                img(ARROW, 20, 20)
            }

            // Render rewards
            renderEntries(data.rewards) { rx, ry, e ->
                renderEntry(this, e, BountyZoneSize + ArrowWidth + rx, ry, true)
            }
        }
    }

    companion object {
        val BUTTON = Identifier("widget/button")
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