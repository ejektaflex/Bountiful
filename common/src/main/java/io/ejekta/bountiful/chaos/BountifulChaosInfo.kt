package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.resources.ResourceLocation

@Serializable
class BountifulChaosInfo(
    var deps: MutableMap<@Contextual ResourceLocation, Int> = mutableMapOf(),
    var unsolved: Int = 0,
    var redundant: MutableList<@Contextual ResourceLocation> = mutableListOf()
)