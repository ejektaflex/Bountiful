package io.ejekta.bountiful.content.gui.widgets

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemLogic
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

class BountyLongButton(val parent: BoardScreen, var bountyIndex: Int) : KWidget(160) {

    private fun getBountyData(): BountyData {
        return BountyData[parent.boardHandler.inventory.getStack(bountyIndex)]
    }

    private fun renderEntry(dsl: KGuiDsl, entry: BountyDataEntry, x: Int, y: Int) {
        when (entry.type) {
            BountyType.ITEM -> {
                val stack = ItemLogic(entry).itemStack.apply {
                    count = entry.amount
                }
                dsl { itemStackIcon(stack, x, y) }
            }
        }
        // Entry tooltip
        dsl {
            onHoverArea(x, y, 18, 18) {
                tooltip(entry.textBoard(MinecraftClient.getInstance().player!!))
            }
        }
    }

    override fun onDraw(dsl: KGuiDsl): KGuiDsl = dsl {
        // Draw button background
        sprite(DEFAULT, w = DEFAULT.width - 42)
        sprite(CAP, DEFAULT.width - 42)

        val data = getBountyData()
        // Render objectives
        for (i in data.objectives.indices) {
            renderEntry(this, data.objectives[i],  i * 20 + 1, 1)
        }
        // Render rewards
        for (i in data.rewards.indices) {
            renderEntry(this, data.rewards[i], width - (20 * (i + 1)), 1)
        }
    }

    companion object {
        val SHEET = KSpriteGrid(Identifier("textures/gui/widgets.png"), 256, 256)
        val DEFAULT = SHEET.Sprite(0f, 66f, 200, 20)
        val CAP = SHEET.Sprite(198f, 66f, 2, 20)
    }

}