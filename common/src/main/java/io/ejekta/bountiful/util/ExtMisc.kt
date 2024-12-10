package io.ejekta.bountiful.util

import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.kambrik.message.KambrikMsg
import net.minecraft.client.MinecraftClient
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.entity.ai.brain.Brain
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.resources.ResourceKey
import net.minecraft.screen.ScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayer
import net.minecraft.text.Text
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.GlobalPos
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradedItem
import net.minecraft.world.World
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

operator fun <T> MinecraftServer.get(regResourceKey: ResourceKey<Registry<T>>): Registry<T> {
    return registryAccess().registry(regResourceKey).get()
}

operator fun <T> RegistryAccess.get(regResourceKey: ResourceKey<Registry<T>>): Registry<T> {
    return registry(regResourceKey).get()
}

fun <T : Any> Registry<T>.getNullable(rl: ResourceLocation): T? {
    return getOptional(rl).getOrNull()
}


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

fun World.everySeconds(secs: Int, offset: Long = 0L, func: () -> Unit) {
    if (((time + (secs * GameTime.TICK_RATE) + offset) % GameTime.TICK_RATE) == 0L) {
        func()
    }
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

fun getTagItemKey(id: ResourceLocation): TagKey<Item> = TagKey.of(Registries.ITEM.key, id)

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

val KambrikMsg.ctx: MinecraftClient
    get() = MinecraftClient.getInstance()

fun ServerPlayer.iterateBountyStacks(func: BountyStack.() -> Unit) {
    inventory.main.filter {
        it.item is BountyItem
    }.map { BountyStack(it) }.forEach(func)
}

fun Brain<*>.ensureMemoryModules(memoryList: List<MemoryModuleType<*>>) {
    val memMM = memories as MutableMap
    for (item in memoryList) {
        if (item !in memMM) {
            memMM[item] = Optional.empty()
        }
    }
}

fun ServerPlayer.openHandledScreenSimple(screenName: Text, handlerFactory: ScreenHandlerFactory): OptionalInt {
    return openHandledScreen(
        SimpleNamedScreenHandlerFactory(
            handlerFactory, screenName
        )
    )
}

fun VillagerEntity.checkOnBoard(boardPos: BlockPos) {
    // Inject memory into memory map, else remembrance will fail
    brain.ensureMemoryModules(listOf(
        BountifulContent.MEM_MODULE_NEAREST_BOARD
    ))
    // Set up villager memory
    brain.remember(
        BountifulContent.MEM_MODULE_NEAREST_BOARD, GlobalPos.create(
        world.registryKey, boardPos
    ))
}

fun VillagerEntity.hackyGiveTradeExperience(amt: Int) {
    trade(
        TradeOffer(TradedItem(Items.AIR), ItemStack.EMPTY, 1, amt, 1f)
    )
}

val ServerPlayer.currentBoardInteracting: BoardBlockEntity?
    get() {
        val shPos = (currentScreenHandler as? BoardScreenHandler)?.inventory?.pos
        shPos?.run {
            serverWorld.getBlockEntity(this)?.let {
                return (it as? BoardBlockEntity)
            }
        }
        return null
    }



