package ejektaflex.bountiful.cap

import ejektaflex.bountiful.logic.BountyHolder
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

class GlobalBoard(override val holder: BountyHolder) : IGlobalBoard, INBTSerializable<NBTTagCompound> by holder