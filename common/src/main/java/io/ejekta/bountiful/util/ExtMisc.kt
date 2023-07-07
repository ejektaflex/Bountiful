package io.ejekta.bountiful.util

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountyItem
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random

fun randomSplit(num: Double, ways: Int): List<Double> {
    val bits = (0 until ways).map { Random.nextDouble() }
    val sum = bits.sum()
    return bits.map { (it / sum) * num }
}

val Inventory.readOnlyCopy: DefaultedList<ItemStack>
    get() = DefaultedList.ofSize(size(), ItemStack.EMPTY).apply {
        (0 until size()).forEach { i -> this[i] = getStack(i) }
    }

fun <T : Any> List<T>.weightedRandomIntBy(func: T.() -> Int): T {
    val mapped = associate { it to func(it) }
    return mapped.weightedRandomInt()
}

fun <T : Any> List<T>.weightedRandomDblBy(func: T.() -> Double): T {
    val mapped = associate { it to func(it) }
    return mapped.weightedRandomDbl()
}

fun <T : Any> Map<T, Int>.weightedRandomInt(): T {
    val sum = values.sum()

    if (sum == 0) {
        return keys.random()
    }

    var point = (1..sum).random()

    for ((item, weight) in this) {
        if (point <= weight) {
            return item
        }
        point -= weight
    }
    return keys.last()
}

fun <T : Any> Map<T, Double>.weightedRandomDbl(): T {
    val sum = values.sum()

    if (sum == 0.0) {
        return keys.random()
    }

    var point = Random.nextDouble(sum)

    for ((item, weight) in this) {
        if (point <= weight) {
            return item
        }
        point -= weight
    }
    return keys.last()
}

fun NbtCompound.putBlockPos(key: String, pos: BlockPos) {
    val posNbt = NbtCompound().apply {
        putInt("x", pos.x)
        putInt("y", pos.y)
        putInt("z", pos.z)
    }
    put(key, posNbt)
}

fun NbtCompound.getBlockPos(key: String): BlockPos {
    val tag = getCompound(key)
    return try {
        BlockPos(
            tag.getInt("x"),
            tag.getInt("y"),
            tag.getInt("z")
        )
    } catch (e: Exception) {
        BlockPos.ORIGIN
    }
}

fun getTagItemKey(id: Identifier): TagKey<Item> = TagKey.of(Registries.ITEM.key, id)

fun getTagItems(reg: DynamicRegistryManager, tagKey: TagKey<Item>): List<Item> {
    return getRegistryTags(reg, tagKey)
}

fun <T> getRegistryTags(reg: DynamicRegistryManager, tagKey: TagKey<T>): List<T> {
    val typedReg = reg[tagKey.registry] ?: return emptyList()
    val streamed = typedReg.streamTagsAndEntries().filter {
        tagKey == it.first
    }.map {
        it.second.toList().map { re ->
            re.value()
        }
    }.toList().flatten()
    return streamed
}

fun ServerPlayerEntity.iterateBountyStacks(func: ItemStack.() -> Unit) {
    inventory.main.filter {
        it.item is BountyItem
    }.forEach(func)
}

fun ServerPlayerEntity.iterateBountyData(func: BountyData.() -> Boolean) {
    iterateBountyStacks {
        BountyData.editIf(this, func)
    }
}






