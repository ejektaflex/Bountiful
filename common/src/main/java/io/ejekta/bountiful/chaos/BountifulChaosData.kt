package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier

@Serializable
class BountifulChaosData(
    var matching: BountifulChaosMatching = BountifulChaosMatching(),
    var required: MutableMap<@Contextual Identifier, Double?> = mutableMapOf(),
    var optional: MutableMap<@Contextual Identifier, Double?> = mutableMapOf()
)
