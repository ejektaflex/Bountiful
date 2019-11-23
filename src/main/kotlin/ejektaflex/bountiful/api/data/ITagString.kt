package ejektaflex.bountiful.api.data

import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.CompoundNBT

interface ITagString {
    var nbtString: String?

    val tag: CompoundNBT?
        get() {
            return when (nbtString) {
                null -> null
                is String -> JsonToNBT.getTagFromJson(nbtString.toString())
                else -> throw Exception("NBT $nbtString must be a String! Instead was a: ${nbtString!!::class}")
            }
        }
}