package io.ejekta.bountiful.content.gui

import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.client.Minecraft
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class AnalyzerDecreeSlot(inv: SimpleContainer, val usingPlayer: Player, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {
    override fun mayPlace(pStack: ItemStack) = pStack.item == BountifulContent.DECREE_ITEM

    override fun mayPickup(pPlayer: Player) = true

    override fun set(stack: ItemStack) {
        super.set(stack)
        if (Kambridge.isOnClient()) {
            val anScreen = Minecraft.getInstance().screen as? AnalyzerScreen
            anScreen?.refreshWidgets()
        }
    }
}
