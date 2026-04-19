package io.ejekta.bountiful.content.board

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.util.readOnlyCopy
import io.ejekta.kambrik.ext.ksx.decodeFromStringTag
import io.ejekta.kambrik.ext.ksx.encodeToStringTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.nbt.StringTag
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.ContainerHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class GlobalBoardData : SavedData() {

    val decrees = SimpleContainer(3)
    val bounties = BountyInventory()
    var bountyTimestamps: MutableMap<Int, Long> = mutableMapOf()
    var lastUpdatedTime: Long = 0L
    var playerData: MutableMap<String, PlayerBoardData> = mutableMapOf()

    @Serializable
    data class PlayerBoardData(var done: Int, var totalTime: Long, val taken: MutableSet<Int>) {
        companion object {
            fun empty() = PlayerBoardData(0, 0L, mutableSetOf())
        }
    }

    fun loadFrom(input: ValueInput) {
        lastUpdatedTime = input.getLongOr("lastUpdated", 0L)

        ContainerHelper.loadAllItems(input.childOrEmpty("decree_inv"), decrees.items)
        ContainerHelper.loadAllItems(input.childOrEmpty("bounty_inv"), bounties.items)

        input.getString("completed").ifPresent { str ->
            playerData = JsonFormats.BlockEntity.decodeFromStringTag(playerDataSerializer, StringTag.valueOf(str)).toMutableMap()
        }
        input.getString("timestamps").ifPresent { str ->
            bountyTimestamps = JsonFormats.BlockEntity.decodeFromStringTag(bountyStampSerializer, StringTag.valueOf(str)).toMutableMap()
        }
    }

    fun saveTo(output: ValueOutput) {
        output.putLong("lastUpdated", lastUpdatedTime)

        output.putString("completed", JsonFormats.BlockEntity.encodeToStringTag(playerDataSerializer, playerData).value())
        output.putString("timestamps", JsonFormats.BlockEntity.encodeToStringTag(bountyStampSerializer, bountyTimestamps).value())

        ContainerHelper.saveAllItems(output.child("decree_inv"), decrees.readOnlyCopy)
        ContainerHelper.saveAllItems(output.child("bounty_inv"), bounties.readOnlyCopy)
    }

    companion object {
        internal val playerDataSerializer = MapSerializer(String.serializer(), PlayerBoardData.serializer())
        private val bountyStampSerializer = MapSerializer(Int.serializer(), Long.serializer())

        private val CODEC: Codec<GlobalBoardData> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.OPTIONAL_CODEC.listOf()
                    .optionalFieldOf("decree_inv", emptyList())
                    .forGetter { data -> (0 until 3).map { data.decrees.getItem(it) } },
                ItemStack.OPTIONAL_CODEC.listOf()
                    .optionalFieldOf("bounty_inv", emptyList())
                    .forGetter { data -> (0 until BoardInventory.BOUNTY_SIZE).map { data.bounties.getItem(it) } },
                Codec.unboundedMap(Codec.STRING, Codec.LONG)
                    .optionalFieldOf("timestamps", emptyMap())
                    .forGetter { data -> data.bountyTimestamps.entries.associate { it.key.toString() to it.value } },
                Codec.LONG.optionalFieldOf("last_updated", 0L)
                    .forGetter(GlobalBoardData::lastUpdatedTime),
                Codec.STRING.optionalFieldOf("completed", "")
                    .forGetter { data ->
                        if (data.playerData.isEmpty()) "" else JsonFormats.BlockEntity.encodeToStringTag(playerDataSerializer, data.playerData).value()
                    }
            ).apply(instance) { decreeList, bountyList, timestamps, lastUpdated, completedStr ->
                GlobalBoardData().also { gbd ->
                    decreeList.forEachIndexed { slot, stack -> gbd.decrees.setItem(slot, stack) }
                    bountyList.forEachIndexed { slot, stack -> gbd.bounties.setItem(slot, stack) }
                    gbd.bountyTimestamps = timestamps.mapKeys { it.key.toInt() }.toMutableMap()
                    gbd.lastUpdatedTime = lastUpdated
                    if (completedStr.isNotEmpty()) {
                        runCatching {
                            gbd.playerData = JsonFormats.BlockEntity.decodeFromStringTag(playerDataSerializer, StringTag.valueOf(completedStr)).toMutableMap()
                        }
                    }
                }
            }
        }

        val TYPE: SavedDataType<GlobalBoardData> = SavedDataType(
            Identifier.fromNamespaceAndPath("bountiful", "global_board"),
            ::GlobalBoardData,
            CODEC,
            DataFixTypes.LEVEL
        )

        fun getOrCreate(server: MinecraftServer): GlobalBoardData {
            return server.dataStorage.computeIfAbsent(TYPE)
        }
    }
}
