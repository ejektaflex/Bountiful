package io.ejekta.bountiful.content.editor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject as KJsonObject
import kotlinx.serialization.json.jsonObject
import net.minecraft.server.level.ServerPlayer

object BountifulEditorPersistence {

    fun savePoolEntry(player: ServerPlayer, payload: PoolEntryEditorPayload) {
        val oldPoolId = payload.originalPoolId ?: payload.poolId
        val oldKey = payload.originalEntryKey ?: payload.entryKey

        val newEntry = buildPoolEntry(player, payload)

        if (oldPoolId != payload.poolId) {
            val oldMap = buildConfigPoolContentMap(oldPoolId).toMutableMap()
            oldMap[oldKey] = null
            writePool(oldPoolId, oldMap)
        }

        val newMap = buildConfigPoolContentMap(payload.poolId).toMutableMap()
        if (oldPoolId == payload.poolId && oldKey != payload.entryKey) {
            newMap[oldKey] = null
        }
        newMap[payload.entryKey] = encodePoolEntry(newEntry)
        writePool(payload.poolId, newMap)
    }

    fun deletePoolEntry(poolId: String, entryKey: String) {
        val newMap = buildConfigPoolContentMap(poolId).toMutableMap()
        newMap[entryKey] = null
        writePool(poolId, newMap)
    }

    fun saveEmptyPool(poolId: String) {
        writePool(poolId, buildConfigPoolContentMap(poolId))
    }

    fun deletePool(poolId: String) {
        val pool = Pool(
            id = poolId,
            replace = true,
            content = mutableMapOf()
        ).apply { setup(poolId) }
        BountifulIO.getPoolFile(poolId).write(pool)
        upsertPool(pool)
    }

    fun saveDecree(payload: DecreeEditorPayload) {
        val originalId = payload.originalId ?: payload.id
        val decree = Decree(
            id = payload.id,
            objectives = parseCsv(payload.objectivesCsv).toMutableSet(),
            rewards = parseCsv(payload.rewardsCsv).toMutableSet(),
            replace = true,
            name = payload.name.ifBlank { null },
            canSpawn = payload.canSpawn,
            canReveal = payload.canReveal,
            canWanderBuy = payload.canWanderBuy,
            linkedProfessions = parseCsv(payload.linkedProfessionsCsv)
        )
        BountifulIO.getDecreeFile(payload.id).write(decree)
        if (originalId != payload.id) {
            BountifulIO.deleteDecreeConfigFile(originalId)
            BountifulContent.Decrees.removeIf { it.id == originalId }
        }
        upsertDecree(decree)
    }

    fun deleteDecree(id: String) {
        val decree = Decree(
            id = id,
            objectives = mutableSetOf(),
            rewards = mutableSetOf(),
            replace = true,
            name = null,
            canSpawn = false,
            canReveal = false,
            canWanderBuy = false,
            linkedProfessions = emptyList()
        )
        BountifulIO.getDecreeFile(id).write(decree)
        upsertDecree(decree)
    }

    private fun buildPoolEntry(player: ServerPlayer, payload: PoolEntryEditorPayload): PoolEntry {
        val existingComponents = payload.existingComponentsJson?.takeIf { it.isNotBlank() }?.let {
            JsonParser.parseString(it).asJsonObject
        }
        val existingConditions = payload.existingConditionsJson?.takeIf { it.isNotBlank() }?.let {
            JsonParser.parseString(it).asJsonObject
        }

        val content = payload.content
        val components = existingComponents

        val json = JsonObject().apply {
            addProperty("type", payload.typeId)
            addProperty("rarity", payload.rarity)
            addProperty("content", content)
            add("amount", JsonObject().apply {
                addProperty("min", payload.amountMin)
                addProperty("max", payload.amountMax)
            })
            addProperty("unitWorth", payload.unitWorth)
            addProperty("weightMult", payload.weightMult)
            addProperty("timeMult", payload.timeMult)
            addProperty("repRequired", payload.repRequired)
            if (payload.name.isNotBlank()) addProperty("name", payload.name)
            if (payload.mystery) addProperty("mystery", true)
            if (components != null) add("components", components)
            if (existingConditions != null) add("conditions", existingConditions)
            add("markers", parseCsv(payload.markersCsv).fold(com.google.gson.JsonArray()) { arr, item -> arr.apply { add(item) } })
            add("forbidMarkers", parseCsv(payload.forbidMarkersCsv).fold(com.google.gson.JsonArray()) { arr, item -> arr.apply { add(item) } })
            add("modifiers", parseCsv(payload.modifiersCsv).fold(com.google.gson.JsonArray()) { arr, item -> arr.apply { add(item) } })
            payload.biomesJson?.takeIf { it.isNotBlank() }?.let { json ->
                runCatching { add("biomes", JsonParser.parseString(json)) }
            }
        }

        return JsonFormats.Config.dynamicDecodeFromString(json.toString(), PoolEntry.serializer()).apply {
            id = "${payload.poolId}.${payload.entryKey}"
        }
    }

    private fun encodePoolEntry(entry: PoolEntry): KJsonObject {
        return Json.parseToJsonElement(JsonFormats.Config.dynamicEncodeToString(entry, PoolEntry.serializer())).jsonObject
    }

    private fun buildConfigPoolContentMap(poolId: String): MutableMap<String, KJsonObject?> {
        val current = BountifulIO.getPoolFile(poolId).read()
        return current.content.toMutableMap()
    }

    private fun writePool(poolId: String, content: MutableMap<String, KJsonObject?>) {
        val pool = Pool(
            id = poolId,
            replace = false,
            content = content
        ).apply { setup(poolId) }
        BountifulIO.getPoolFile(poolId).write(pool)
        upsertPool(pool)
    }

    private fun upsertPool(pool: Pool) {
        val merged = buildMergedPool(pool.id)?.let {
            BountifulContent.Pools.filter { existing -> existing.id != pool.id } + it
        } ?: BountifulContent.Pools.filter { existing -> existing.id != pool.id }
        BountifulContent.populatePools(merged.sortedBy { it.id })
    }

    private fun buildMergedPool(poolId: String): Pool? {
        val resourcePool = BountifulContent.Pools.find { it.id == poolId }
        val configPool = BountifulIO.getPoolFile(poolId).read().apply { setup(poolId) }
        val merged = when {
            configPool.replace -> configPool
            resourcePool != null -> resourcePool.merged(configPool)
            else -> configPool
        }
        return merged.apply { setup(poolId) }.takeIf {
            !(configPool.replace && configPool.content.isEmpty())
                    && (it.items.isNotEmpty() || it.content.isNotEmpty() || resourcePool != null)
        }
    }

    private fun upsertDecree(decree: Decree) {
        BountifulContent.Decrees.removeIf { it.id == decree.id }
        BountifulContent.Decrees.add(decree)
        BountifulContent.Decrees.sortBy { it.id }
    }

    fun parseCsv(input: String): List<String> {
        return input.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    }
}
