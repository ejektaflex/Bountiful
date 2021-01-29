package io.ejekta.bountiful.common.mixin

import net.minecraft.nbt.LongTag
import net.minecraft.nbt.Tag

object MixinHelpers {
    fun longsToTags(longs: LongArray): List<Tag> {
        return longs.map { LongTag.of(it) }
    }

}