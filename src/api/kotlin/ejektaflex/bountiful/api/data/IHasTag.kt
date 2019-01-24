package ejektaflex.bountiful.api.data

import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound

interface IHasTag {
    var nbtJson: Any?

    val tag: NBTTagCompound?
        get() {
            return when (nbtJson) {
                null -> null
                is String -> JsonToNBT.getTagFromJson(nbtJson.toString())
                is NBTTagCompound -> nbtJson as NBTTagCompound
                else -> throw Exception("NBT $nbtJson must be a String! Instead was a: ${nbtJson!!::class}")
            }
        }
}