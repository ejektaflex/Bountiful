package ejektaflex.bountiful.api.data

import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound

interface IHasTag {
    var nbtJson: String?

    val tag: NBTTagCompound?
        get() {
            return when (nbtJson) {
                null -> null
                is String -> JsonToNBT.getTagFromJson(nbtJson.toString())
                else -> throw Exception("NBT $nbtJson must be a String! Instead was a: ${nbtJson!!::class}")
            }
        }
}