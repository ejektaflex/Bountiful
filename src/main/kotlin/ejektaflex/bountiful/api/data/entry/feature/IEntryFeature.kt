package ejektaflex.bountiful.api.data.entry.feature

import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

interface IEntryFeature {
    fun serializeNBT(tag: CompoundNBT)
    fun deserializeNBT(tag: CompoundNBT)
}