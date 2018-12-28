package ejektaflex.bountiful.world

import ejektaflex.bountiful.BountifulInfo
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData

class BountyWorldData(inName: String) : WorldSavedData(inName) {

    constructor() : this(dataID)

    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        return tag
    }

    override fun readFromNBT(tag: NBTTagCompound) {

    }

    companion object {
        val dataID = BountifulInfo.MODID + "_DATA"

        operator fun get(world: World): BountyWorldData {
            val storage = world.mapStorage
            var instance: BountyWorldData? = storage!!.getOrLoadData(BountyWorldData::class.java, dataID) as BountyWorldData?
            if (instance == null) {
                instance = BountyWorldData()
                instance.markDirty()
                storage.setData(dataID, instance)
            }
            return instance
        }

    }

}