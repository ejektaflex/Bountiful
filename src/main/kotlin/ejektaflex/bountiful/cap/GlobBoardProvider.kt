package ejektaflex.bountiful.cap

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import javax.annotation.Nullable


class GlobBoardProvider : ICapabilitySerializable<NBTTagCompound> {

    private val inst = GlobalBoard()

    override fun serializeNBT(): NBTTagCompound {
        return CapManager.CAP_BOARD?.storage!!.writeNBT(CapManager.CAP_BOARD, inst, EnumFacing.UP) as NBTTagCompound
    }

    override fun deserializeNBT(tag: NBTTagCompound) {
        CapManager.CAP_BOARD?.storage!!.readNBT(CapManager.CAP_BOARD, inst, EnumFacing.UP, tag)
    }

    // Return capability instance
    override fun <T> getCapability(capability: Capability<T>, @Nullable facing: EnumFacing?): T? {
        return if (capability === CapManager.CAP_BOARD) CapManager.CAP_BOARD.cast(inst) else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapManager.CAP_BOARD
    }


}