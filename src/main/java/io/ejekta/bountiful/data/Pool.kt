package io.ejekta.bountiful.data

import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kudzu.KudzuVine
import io.ejekta.kudzu.toJsonObject
import io.ejekta.kudzu.toKudzu
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

@Serializable
data class Pool(
    @Transient override var id: String = "DEFAULT_POOL",
    val weightMult: Double = 1.0,
    override val replace: Boolean = false,
    override val requires: MutableList<String> = mutableListOf(),
    val content: MutableMap<String, JsonObject?> = mutableMapOf()
) : IMerge<Pool> {

    val items: MutableList<PoolEntry> = mutableListOf()

    fun setup(newId: String) {
        id = newId

        for ((key, value) in content) {
            if (value != null) {
                val pe = JsonFormats.Hand.decodeFromString(PoolEntry.serializer(), value.toString()).apply {
                    this.id = key
                }
                // Don't insert entries with no type set
                if (pe.typeLogic != null) {
                    items.add(pe)
                }
            }
        }
    }

    override fun finishMergedSetup() {
        // Do weight normalization
        val overallMult = items.size
        for (item in items) {
            item.weightMult /= overallMult
            item.weightMult *= this.weightMult
        }
    }

    operator fun iterator() = items.iterator()

    val usedInDecrees: List<Decree>
        get() = BountifulContent.Decrees.filter { this.id in it.allPoolIds }

    override fun merged(other: Pool): Pool {

        val newContent = content.toMutableMap()

        println("Merging $id with ${other.id}")

        for ((otherKey, otherValue) in other.content) {

            if (otherKey !in newContent) { // Add new item with merge
                newContent[otherKey] = otherValue
            } else if (otherValue == null) { // Overwrite with null
                newContent[otherKey] = null
            } else {

                println("Merge candidate: ${content[otherKey]}")
                println("Merge new edits: $otherValue")

                // key is in new content, must graft merge
                val currSrc = content[otherKey]?.toKudzu() ?: KudzuVine()
                val otherSrc = otherValue.toKudzu()


                val graftedVine = currSrc.clone().apply {
                    graft(otherSrc)
                }

                newContent[otherKey] = graftedVine.toJsonObject()

                println("Newly Merged: ${newContent[otherKey]}")
            }

        }

        return other.copy(id = id, content = newContent).apply {
            setup(id)
        }
    }

}