package ejektaflex.bountiful.api.data.entry.feature

import net.minecraft.nbt.CompoundNBT

interface IKilledAmount : IEntryFeature {
    var killedAmount: Int

    override fun deserializeNBT(p0: CompoundNBT) {
        killedAmount = p0.getInt("killedAmount")
    }

    override fun serializeNBT(tag: CompoundNBT) {
        tag.putInt("killedAmount", killedAmount)
    }
}