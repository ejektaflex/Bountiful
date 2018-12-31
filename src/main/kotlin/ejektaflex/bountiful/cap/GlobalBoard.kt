package ejektaflex.bountiful.cap

import ejektaflex.bountiful.logic.BountyHolder
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.ItemStackHandler

class GlobalBoard(override val holder: BountyHolder = BountyHolder(ItemStackHandler(27))) : IGlobalBoard, INBTSerializable<NBTTagCompound> by holder