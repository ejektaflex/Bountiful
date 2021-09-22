package io.ejekta.bountiful.content.gui.widgets

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.EntityLogic
import io.ejekta.bountiful.bounty.logic.ItemLogic
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.kambrik.ext.fapi.textRenderer
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier

class BountyLongButton(val parent: BoardScreen, var bountyIndex: Int) : KWidget(160, 20) {

    fun getBountyData(): BountyData {
        return BountyData[parent.boardHandler.inventory.getStack(bountyIndex)]
    }

    private fun renderEntry(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int, isReward: Boolean = false) {
        when (entry.type) {
            BountyType.ITEM -> {
                val stack = ItemLogic(entry).itemStack.apply {
                    count = entry.amount
                }
                dsl { itemStackIcon(stack, x, y) }
            }
            BountyType.ENTITY -> {
                val entityType = EntityLogic(entry).entityType

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
        }
        // Render amount
        dsl {
            val textToShow = textLiteral(entry.amount.toString()) {
                color = if (isReward) {
                    entry.rarity.color.colorValue!!
                } else {
                    0xFFFFFF
                }
            }
            textImmediate(x + 17 - ctx.screen.textRenderer.getWidth(textToShow.string) * 2, y + 9, textToShow)
        }
        // Entry tooltip
        dsl {
            areaOnHover(x, y, 18, 18) {
                tooltip(entry.textBoard(MinecraftClient.getInstance().player!!))
            }
        }
    }

    override fun onDraw(dsl: KGuiDsl): KGuiDsl = dsl {
        // Draw button background
        sprite(DEFAULT, w = DEFAULT.width - 42)
        sprite(CAP, DEFAULT.width - 42)

        area(width, height) {
            rect(0xb86f50, 0x48)
            onHover {
                rect(0xFFFFFF, 0x33)
            }
        }

        val data = getBountyData()

        // Render objectives
        for (i in data.objectives.indices) {
            renderEntry(this, data.objectives[i],  i * 20 + 1, 1)
        }
        // Render rewards
        for (i in data.rewards.indices) {
            renderEntry(this, data.rewards[i], width - (20 * (i + 1)), 1, isReward = true)
        }
    }

    companion object {
        val SHEET = KSpriteGrid(Identifier("textures/gui/widgets.png"), 256, 256)
        val DEFAULT = SHEET.Sprite(0f, 66f, 200, 20)
        val CAP = SHEET.Sprite(198f, 66f, 2, 20)
    }

}