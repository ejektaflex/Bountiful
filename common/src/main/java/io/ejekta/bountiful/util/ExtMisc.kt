package io.ejekta.bountiful.util

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.json.JsonElement
import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.Container
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.Level
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

operator fun <T> MinecraftServer.get(regResourceKey: ResourceKey<Registry<T>>): Registry<T> {
    return registryAccess().registry(regResourceKey).get()
}

operator fun <T> RegistryAccess.get(regResourceKey: ResourceKey<out Registry<T>>): Registry<T> {
    return registry(regResourceKey).get()
}

fun <T : Any> Registry<T>.getNullable(rl: ResourceLocation): T? {
    return getOptional(rl).getOrNull()
}

fun ItemStack.asComponentJson(lookup: HolderLookup.Provider): JsonObject? {
    val regOps = RegistryOps.create(JsonOps.INSTANCE, lookup)
    val result = ItemStack.CODEC.encodeStart(regOps, this).resultOrPartial().getOrNull()?.asJsonObject
    return result?.get("components")?.asJsonObject
}

fun randomSplit(num: Double, ways: Int): List<Double> {
    val bits = (0 until ways).map { Random.nextDouble() }
    val sum = bits.sum()
    return bits.map { (it / sum) * num }
}

val Container.readOnlyCopy: NonNullList<ItemStack>
    get() = NonNullList.withSize(containerSize, ItemStack.EMPTY).apply {
        (0 until containerSize).forEach { i -> this[i] = getItem(i) }
    }

fun <T : Any> List<T>.weightedRandomIntBy(func: T.() -> Int): T {
    val mapped = associate { it to func(it) }
    return mapped.weightedRandomInt()
}

fun <T : Any> List<T>.weightedRandomDblBy(func: T.() -> Double): T {
    val mapped = associate { it to func(it) }
    return mapped.weightedRandomDbl()
}

fun Level.everySeconds(secs: Int, offset: Long = 0L, func: () -> Unit) {
    if (((gameTime + (secs * GameTime.TICK_RATE) + offset) % GameTime.TICK_RATE) == 0L) {
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

fun CompoundTag.putBlockPos(key: String, pos: BlockPos) {
    val posNbt = CompoundTag().apply {
        putInt("x", pos.x)
        putInt("y", pos.y)
        putInt("z", pos.z)
    }
    put(key, posNbt)
}

fun CompoundTag.getBlockPos(key: String): BlockPos {
    val tag = getCompound(key)
    return try {
        BlockPos(
            tag.getInt("x"),
            tag.getInt("y"),
            tag.getInt("z")
        )
    } catch (e: Exception) {
        BlockPos.ZERO
    }
}

fun getTagItemKey(id: ResourceLocation): TagKey<Item> = TagKey.create(BuiltInRegistries.ITEM.key(), id)

fun getTagItems(reg: RegistryAccess, tagKey: TagKey<Item>): List<Item> {
    return getRegistryTags(reg, tagKey)
}

fun <T : Any> getRegistryTags(reg: RegistryAccess, tagKey: TagKey<T>): List<T> {
    val typedReg = reg[tagKey.registry] ?: return emptyList()
    val streamed = typedReg.tags.filter {
        tagKey == it.first
    }.map {
        it.second.toList().map { re ->
            re.value()
        }
    }.toList().flatten()
    return streamed
}

val KambrikMsg.ctx: Minecraft
    get() = Minecraft.getInstance()

fun ServerPlayer.iterateBountyStacks(func: BountyStack.() -> Unit) {
    inventory.items.filter {
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

fun ServerPlayer.openSimpleMenu(screenName: Component, handlerFactory: MenuConstructor): OptionalInt {
    return openMenu(SimpleMenuProvider(handlerFactory, screenName))
}

fun Villager.checkOnBoard(boardPos: BlockPos) {
    // Inject memory into memory map, else remembrance will fail
    brain.ensureMemoryModules(listOf(
        BountifulContent.MEM_MODULE_NEAREST_BOARD
    ))
    // Set up villager memory
    brain.setMemory(
        BountifulContent.MEM_MODULE_NEAREST_BOARD, GlobalPos.of(
        level().dimension(), boardPos
    ))
}

fun Villager.hackyGiveTradeExperience(amt: Int) {
    notifyTrade(MerchantOffer(ItemCost(Items.AIR), ItemStack.EMPTY, 1, amt, 1f))
}

val ServerPlayer.currentBoardInteracting: BoardBlockEntity?
    get() {
        val shPos = (containerMenu as? BoardScreenHandler)?.container?.pos
        shPos?.run {
            serverLevel().getBlockEntity(this)?.let {
                return (it as? BoardBlockEntity)
            }
        }
        return null
    }



