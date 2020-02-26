package ejektaflex.bountiful.gui

import com.mojang.blaze3d.platform.GlStateManager
import ejektaflex.bountiful.BountifulMod
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect

class BoardScreen(container: BoardContainer, inv: PlayerInventory, name: ITextComponent) : ContainerScreen<BoardContainer>(container, inv, name) {

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        this.minecraft!!.textureManager.bindTexture(BACKGROUND)
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        blit(x, y, 0, 0, width, height)
        // TODO super?
    }

    /*
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        this.minecraft!!.textureManager.bindTexture(BACKGROUND)
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        this.blit(x, y, 0, 0, width, height)
        super.render(mouseX, mouseY, partialTicks)
    }

     */

    companion object {
        private val BACKGROUND = ResourceLocation(BountifulMod.MODID, "textures/gui/container/bounty_board.png")
    }

}