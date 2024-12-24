package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.resources.ResourceLocation

@Serializable
class BountifulChaosData(
    var matching: BountifulChaosMatching = BountifulChaosMatching(),
    var required: MutableMap<@Contextual ResourceLocation, Double?> = mutableMapOf(),
    var optional: MutableMap<@Contextual ResourceLocation, Double?> = mutableMapOf()
)