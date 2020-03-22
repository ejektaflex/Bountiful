package ejektaflex.bountiful.gui

import com.mojang.blaze3d.platform.GlStateManager
import ejektaflex.bountiful.BountifulMod
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent

class BoardScreen(container: BoardContainer, inv: PlayerInventory, name: ITextComponent) : ContainerScreen<BoardContainer>(container, inv, name) {

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        // 'Bounty Board'
        val name = I18n.format("block.bountiful.bountyboard")
        font.drawString(name, xSize / 2 - font.getStringWidth(name) - 12f, 6f, 0x170e0b)

        // 'Inventory'
        font.drawString(playerInventory.displayName.unformattedComponentText, 8f, ySize - 93f, 0x170e0b)
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        this.minecraft!!.textureManager.bindTexture(BACKGROUND)
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        blit(x, y, 0, 0, xSize, ySize)
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    companion object {
        private val BACKGROUND = ResourceLocation(BountifulMod.MODID, "textures/gui/container/bounty_board.png")
    }

}