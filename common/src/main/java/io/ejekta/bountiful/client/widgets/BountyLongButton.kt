package io.ejekta.bountiful.client.widgets

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeEntity
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItemTag
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.content.messages.SelectBounty
import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KSpriteGrid
import io.ejekta.bountiful.kambrik.gui.KWidget
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
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

    override val width: Int = 160
    override val height: Int = 20

    fun getBountyData(): BountyData {
        return BountyData[parent.boardHandler.inventory.getStack(bountyIndex)]
    }

    val reactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            parent.boardHandler.inventory.select(bountyIndex)
            SelectBounty(bountyIndex, MinecraftClient.getInstance().player!!.uuidAsString).sendToServer()
        }
    }

    private fun renderEntryBasedOnLogic(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int, isReward: Boolean) {
        when (entry.logicId) {
            BountyTypeRegistry.COMMAND.id -> {
                dsl { itemStackIcon(ItemStack(Items.COMMAND_BLOCK), x, y) }
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

                dsl {
                    livingEntity(entityType as? EntityType<out LivingEntity>
                        ?: throw Exception("Bounty cannot have ${entry.content} as entity objective, it is not a LivingEntity!"),
                        x + 7,
                        y + 15,
                        size = 15.0
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

    fun isSelected(): Boolean {
        return ItemStack.areEqual(parent.boardHandler.inventory.getStack(-1), parent.boardHandler.inventory.getStack(bountyIndex))
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area.reactWith(reactor)
        area.dsl {
            // Draw button background
            sprite(DEFAULT, w = DEFAULT.width - 42)
            sprite(CAP, DEFAULT.width - 42)

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
            for (i in data.objectives.indices) {
                renderEntry(this, data.objectives[i], i * 20 + 1, 1)
            }
            // Render rewards
            for (i in data.rewards.indices) {
                renderEntry(this, data.rewards[i], width - (20 * (i + 1)), 1, isReward = true)
            }
        }
    }

    companion object {
        val SHEET = KSpriteGrid(Identifier("textures/gui/widgets.png"), 256, 256)
        val DEFAULT = SHEET.Sprite(0f, 66f, 200, 20)
        val CAP = SHEET.Sprite(198f, 66f, 2, 20)
    }

}