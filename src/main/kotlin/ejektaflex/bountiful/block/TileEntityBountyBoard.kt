package ejektaflex.bountiful.block


import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent
import ejektaflex.bountiful.api.ext.*
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.api.ext.clear
import ejektaflex.bountiful.api.ext.filledSlots
import ejektaflex.bountiful.api.ext.slotRange
import ejektaflex.bountiful.cap.CapManager
import ejektaflex.bountiful.cap.GlobBoardProvider
import ejektaflex.bountiful.cap.GlobalBoard
import ejektaflex.bountiful.cap.IGlobalBoard
import ejektaflex.bountiful.logic.BountyData
import ejektaflex.bountiful.logic.BountyHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler



class TileEntityBountyBoard : TileEntity(), ITileEntityBountyBoard {

    private val internalInv = BountyHolder(ItemStackHandler(numSlots))

    val isLocal: Boolean
        get() = !Bountiful.config.globalBounties

    val cap: IGlobalBoard?
        get() = world.getCapability(CapManager.CAP_BOARD!!, null)

    override val inventory: BountyHolder
        get() {
            return if (!isLocal) {
                if (world.hasCapability(CapManager.CAP_BOARD!!, null)) {
                    cap!!.holder
                } else {
                    internalInv
                }
            } else {
                internalInv
            }
        }

    override var newBoard = true

    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        if (isLocal) {
            tag.clear()
            tag.setTag("inv", inventory.handler.serializeNBT())
            tag.setBoolean("newBoard", newBoard)
        }
        return super.writeToNBT(tag)
    }

    override fun readFromNBT(tag: NBTTagCompound) {
        if (isLocal) {
            inventory.handler.deserializeNBT(tag.getCompoundTag("inv"))
            newBoard = tag.getBoolean("newBoard")
        }
        super.readFromNBT(tag)
    }

    override fun update() {
        if (!world.isRemote) {
            // Skip placement update tick
            if (newBoard) {
                newBoard = false
                return
            }

            // Only do tile updates if it's a local inventory
            if (isLocal) {
                val dirty = inventory.update(world, this)
                if (dirty) {
                    markDirty()
                }
            }

        }
    }


    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) inventory.handler as T else {
            super.getCapability<T>(capability, facing)
        }
    }

    companion object {
        const val numSlots = 27
    }

}
