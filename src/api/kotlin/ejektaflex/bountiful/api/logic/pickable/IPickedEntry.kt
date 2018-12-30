package ejektaflex.bountiful.api.logic.pickable

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IPickedEntry : INBTSerializable<NBTTagCompound> {
    var content: String
    var amount: Int
    fun typed(): IPickedEntry
    val contentObj: Any?
    val prettyContent: String
}