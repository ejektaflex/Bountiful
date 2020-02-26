package ejektaflex.bountiful.gui

import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class BoardScreen(container: BoardContainer, inv: PlayerInventory, name: ITextComponent) : ContainerScreen<BoardContainer>(container, inv, name) {

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}