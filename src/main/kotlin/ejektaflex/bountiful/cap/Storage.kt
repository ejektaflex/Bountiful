package ejektaflex.bountiful.cap

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class Storage : Capability.IStorage<IGlobalBoard> {

    override fun writeNBT(capability: Capability<IGlobalBoard>, instance: IGlobalBoard, side: EnumFacing): NBTBase {
        return instance.serializeNBT()
    }

    override fun readNBT(capability: Capability<IGlobalBoard>, instance: IGlobalBoard, side: EnumFacing, nbt: NBTBase) {
        instance.deserializeNBT(nbt as NBTTagCompound)
    }

}
