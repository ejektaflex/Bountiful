package ejektaflex.bountiful.api.data.entry.feature

import net.minecraft.nbt.CompoundNBT

interface IAmount : IEntryFeature {

    var amount: Int

    override fun deserializeNBT(p0: CompoundNBT) {
        amount = p0.getInt("amount")
    }

    override fun serializeNBT(tag: CompoundNBT) {
        tag.putInt("amount", amount)
    }

}