package io.ejekta.bountiful.bounty

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CriteriaData(val criterion: JsonObject, val description: String)