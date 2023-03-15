package ejektaflex.bountiful.gui

import com.mojang.blaze3d.vertex.PoseStack
import ejektaflex.bountiful.BountifulMod
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.resources.I18n
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class BoardScreen(container: BoardMenu, inv: Inventory, name: Component) : AbstractContainerScreen<BoardMenu>(container, inv, name) {

    override fun drawGuiContainerForegroundLayer(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        // 'Bounty Board'
        val name = I18n.format("block.bountiful.bountyboard")

        font.func_243246_a(
            poseStack,
            Component.translatable("block.bountiful.bountyboard"),
            xSize / 2 - font.getStringWidth(name) - 12f,
            6f,
            0xEADAB5
        )

        // 'Inventory'
        font.func_243246_a(matrixStack, playerInventory.displayName, 8f, ySize - 93f, 0xEADAB5)
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY)
    }

    override fun renderBg(poseStack: PoseStack, partialTicks: Float, x: Int, y: Int) {
        this.minecraft!!.textureManager.bindTexture(BACKGROUND)
        val nx = (width - xSize) / 2
        val ny = (height - ySize) / 2
        blit(poseStack, nx, ny, 0, 0, xSize, ySize)
    }


    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
        renderHoveredTooltip(poseStack, mouseX, mouseY)
    }



    companion object {
        private val BACKGROUND = ResourceLocation(BountifulMod.MODID, "textures/gui/container/bounty_board.png")
    }

}