package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.minecraft.resources.ResourceLocation

@Serializable
class BountifulChaosInfo(
    var deps: MutableMap<@Contextual ResourceLocation, Set<@Contextual ResourceLocation>> = mutableMapOf(),
    var unsolvedList: List<@Contextual ResourceLocation> = mutableListOf(),
    var redundant: MutableList<@Contextual ResourceLocation> = mutableListOf()
)