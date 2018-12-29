package ejektaflex.bountiful.api.logic.pickable

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IPickedEntry : INBTSerializable<NBTTagCompound> {
    var contentID: String
    var amount: Int
    val pretty: String
    fun typed(): IPickedEntry
    val content: Any?
}