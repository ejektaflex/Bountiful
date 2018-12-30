package ejektaflex.bountiful.cap

import ejektaflex.bountiful.logic.BountyHolder
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.items.ItemStackHandler
import javax.annotation.Nullable


class GlobBoardProvider : ICapabilitySerializable<NBTTagCompound> {

    //private

    override fun serializeNBT(): NBTTagCompound {
        return CapManager.CAP_BOARD?.writeNBT(inst, null) as NBTTagCompound
    }

    override fun deserializeNBT(tag: NBTTagCompound) {
        CapManager.CAP_BOARD?.readNBT(inst, null, tag)
    }

    // Return capability instance
    override fun <T> getCapability(capability: Capability<T>, @Nullable facing: EnumFacing?): T? {
        return if (capability === CapManager.CAP_BOARD) CapManager.CAP_BOARD.cast(inst) else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapManager.CAP_BOARD
    }

    companion object {
        val inst = GlobalBoard(BountyHolder(ItemStackHandler(27)))
    }

}