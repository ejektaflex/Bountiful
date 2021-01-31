package io.ejekta.bountiful.common.util

import io.ejekta.bountiful.common.serial.Format
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.random.Random

fun isClient(): Boolean = FabricLoader.getInstance().environmentType == EnvType.CLIENT

fun clientWorld(): World? {
    return if (isClient()) {
        MinecraftClient.getInstance().world
    } else {
        null
    }
}

val ItemStack.identifier: Identifier
    get() = Registry.ITEM.getId(item)

// Shorthand
val ItemStack.id: Identifier
    get() = identifier


fun <T : Any> List<T>.weightedRandomBy(func: T.() -> Int): T {
    val mapped = map { it to func(it) }.toMap()
    return mapped.weightedRandomInt()
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