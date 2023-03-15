package ejektaflex.bountiful.gui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import ejektaflex.bountiful.BountifulMod
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.util.text.TranslationTextComponent

class BoardScreen(container: BoardContainer, inv: PlayerInventory, name: Component) : ContainerScreen<BoardContainer>(container, inv, name) {

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        // 'Bounty Board'
        val name = I18n.format("block.bountiful.bountyboard")

        font.func_243246_a(
            matrixStack,
            Component.translatable("block.bountiful.bountyboard"),
            xSize / 2 - font.getStringWidth(name) - 12f,
            6f,
            0xEADAB5
        )

        // 'Inventory'
        font.func_243246_a(matrixStack, playerInventory.displayName, 8f, ySize - 93f, 0xEADAB5)
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        this.minecraft!!.textureManager.bindTexture(BACKGROUND)
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }


    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        renderHoveredTooltip(matrixStack, mouseX, mouseY)
    }



    companion object {
        private val BACKGROUND = ResourceLocation(BountifulMod.MODID, "textures/gui/container/bounty_board.png")
    }

}