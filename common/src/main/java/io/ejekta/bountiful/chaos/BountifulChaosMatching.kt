package io.ejekta.bountiful.chaos

import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.resources.ResourceLocation

@Serializable
class BountifulChaosMatching(
    val tag: MutableMap<@Contextual ResourceLocation, Double> = mutableMapOf(),
    val regex: MutableMap<String, Double> = mutableMapOf(),
    val ignoreRegex: MutableMap<String, Boolean> = mutableMapOf()
) {
    fun matchCost(item: Item, server: MinecraftServer): Double? {

        val matchedRegex = regex.keys.sorted().firstOrNull {
            Regex(it).matches(item.identifier.toString())
        }
        if (matchedRegex != null) {
            return regex[matchedRegex]!!
        }

        val matchedTag = tag.keys.sorted().firstOrNull {
            item in getTagItems(server.registryManager, getTagItemKey(it))
        }
        if (matchedTag != null) {
            return tag[matchedTag]!!
        }

        return null
    }

    fun isIgnored(item: Item): Boolean {
        val matchedIgnoreRegex = ignoreRegex.filter { it.value }.map { it.key }.sorted().any {
            Regex(it).matches(item.identifier.toString())
        }
        return matchedIgnoreRegex
    }
}