package io.ejekta.bountiful.common.bounty

import net.minecraft.nbt.CompoundTag

interface INbtSave {
    fun save(): CompoundTag
}