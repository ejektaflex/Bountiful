package io.ejekta.bountiful.content.editor

import kotlinx.serialization.Serializable

@Serializable
data class PoolEntryEditorPayload(
    val poolId: String,
    val originalPoolId: String? = null,
    val entryKey: String,
    val originalEntryKey: String? = null,
    val typeId: String,
    val rarity: String,
    val content: String,
    val amountMin: Int,
    val amountMax: Int,
    val unitWorth: Double,
    val weightMult: Double,
    val timeMult: Double,
    val repRequired: Double,
    val markersCsv: String = "",
    val forbidMarkersCsv: String = "",
    val modifiersCsv: String = "",
    val name: String = "",
    val mystery: Boolean = false,
    val biomesJson: String? = null,
    val existingComponentsJson: String? = null,
    val existingConditionsJson: String? = null
)

@Serializable
data class DecreeEditorPayload(
    val id: String,
    val originalId: String? = null,
    val name: String = "",
    val objectivesCsv: String = "",
    val rewardsCsv: String = "",
    val linkedProfessionsCsv: String = "",
    val canSpawn: Boolean = true,
    val canReveal: Boolean = true,
    val canWanderBuy: Boolean = true
)
