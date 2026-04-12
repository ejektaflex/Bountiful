package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.minecraft.resources.Identifier

@Serializable
class BountifulChaosInfo(
    var deps: MutableMap<@Contextual Identifier, Set<@Contextual Identifier>> = mutableMapOf(),
    var unsolvedList: List<@Contextual Identifier> = mutableListOf(),
    var redundant: MutableList<@Contextual Identifier> = mutableListOf()
)
