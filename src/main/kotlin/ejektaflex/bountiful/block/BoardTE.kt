package ejektaflex.bountiful.block

import ejektaflex.bountiful.api.ext.clear
import ejektaflex.bountiful.content.ModContent
import ejektaflex.bountiful.gui.BoardContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class BoardTE : TileEntity(ModContent.Blocks.BOUNTYTILEENTITY), ITickableTileEntity, INamedContainerProvider {


    // Lazy load lazy optional ( ... :| )
    private val lazyOptional: LazyOptional<*> by lazy {
        LazyOptional.of { handler }
    }

    val handler: ItemStackHandler  by lazy {
        ItemStackHandler(SIZE)
    }


    override fun tick() {
        if (!world!!.isRemote) {
            if (world!!.gameTime % 20 == 0L) {
                println("BountyTE::tick")
                markDirty()
            }
        }
    }

    var newBoard = true
    var pulseLeft = 0


    override fun write(compound: CompoundNBT): CompoundNBT {
        return super.write(compound).apply {
            put("inv", handler.serializeNBT())
            putBoolean("newBoard", newBoard)
            putInt("pulseLeft", pulseLeft)
        }
    }

    override fun read(nbt: CompoundNBT) {
        super.read(nbt)
        handler.deserializeNBT(nbt.getCompound("inv"))
        newBoard = nbt.getBoolean("newBoard")
        pulseLeft = nbt.getInt("pulseLeft")
    }

    // Remove side when you want to remove hopper access
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyOptional as LazyOptional<T>
        }

        return super.getCapability(cap)
    }

    override fun createMenu(i: Int, inv: PlayerInventory, player: PlayerEntity): Container? {
        return BoardContainer(i, world!!, pos, inv)
    }

    override fun getDisplayName(): ITextComponent {
        return TranslationTextComponent("block.bountiful.bountyboard")
    }

    companion object {
        const val SIZE = 24
    }

}