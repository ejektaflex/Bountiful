package ejektaflex.bountiful.cap

import ejektaflex.bountiful.logic.BountyHolder
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IGlobalBoard : INBTSerializable<NBTTagCompound> {
    val holder: BountyHolder
}