package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.ext.clear
import ejektaflex.bountiful.content.ModContent
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class BountyTE : TileEntity(ModContent.Blocks.BOUNTYTILEENTITY), ITickableTileEntity {

    private fun genHandler(): ItemStackHandler {
        return ItemStackHandler(1)
    }

    // Lazy load lazy optional ( ... :| )
    private val lazyOptional: LazyOptional<*> by lazy {
        LazyOptional.of { handler }
    }

    private var handler: ItemStackHandler = genHandler()

    override fun tick() {
        if (!world!!.isRemote) {
            if (world!!.gameTime % 20 == 0L) {
                println("BountyTE::tick")
            }
        }
    }

    var newBoard = true
    var pulseLeft = 0

    override fun serializeNBT(): CompoundNBT {
        return super.serializeNBT().apply {
            clear()
            put("inv", handler.serializeNBT())
            putBoolean("newBoard", newBoard)
            putInt("pulseLeft", pulseLeft)
        }
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        super.deserializeNBT(nbt)
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

}