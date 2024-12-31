package io.ejekta.bountiful.content

import com.mojang.serialization.Codec
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.advancement.SimpleCriterion
import io.ejekta.bountiful.components.*
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.core.GlobalPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.stats.StatFormatter
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.function.BiPredicate

object BountifulContent : KambrikAutoRegistrar {

    override fun getId() = "bountiful"

    val Decrees = mutableListOf<Decree>()

    var Pools = listOf<Pool>()
        private set

    var PoolMap = mapOf<String, Pool>()
        private set

    var PoolEntryMap = mapOf<String, PoolEntry>()
        private set

    internal fun populatePools(newPools: List<Pool>) {
        Pools = newPools
        PoolMap = Pools.associateBy { it.id }
        PoolEntryMap = Pools.map { it.items }.flatten().associateBy { it.id }
    }

    fun getDecrees(ids: Set<String>): Set<Decree> {
        return ids.mapNotNull { id ->
            Decrees.find { it.id == id }
        }.toSet()
    }

    val BOUNTY_ITEM by "bounty" forItem { BountyItem() }

    val DECREE_ITEM by "decree" forItem { DecreeItem() }

    val BOARD = "bountyboard" forBlock { BoardBlock() }

    val BOARD_ITEM by "bountyboard" forItem { BlockItem(BOARD.value, Item.Properties().stacksTo(1).fireResistant()) }

    val BOARD_ENTITY by "board-be".forBlockEntity(BOARD, ::BoardBlockEntity)

    val BOARD_SCREEN_HANDLER by "board" forScreen ::BoardScreenHandler

    val ANALYZER_SCREEN_HANDLER by "analyzer" forScreen ::AnalyzerScreenHandler

    val MEM_MODULE_NEAREST_BOARD_INSTANCE = "nearest_bounty_board".forRegistration(
        BuiltInRegistries.MEMORY_MODULE_TYPE
    ) { MemoryModuleType(Optional.empty<Codec<GlobalPos>>()) } as Lazy<MemoryModuleType<GlobalPos>>

    val MEM_MODULE_NEAREST_BOARD by MEM_MODULE_NEAREST_BOARD_INSTANCE

    //val POI_BOUNTY_BOARD = "bountyboard".forVillagerPoi(MEM_MODULE_NEAREST_BOARD_INSTANCE, setOf(BOARD.value.defaultState), 1, 1)

    val BOUNTY_INFO by "bounty_info".forComponent(BountyInfo.serializer())
    val BOUNTY_PING by "bounty_ping".forComponent(Boolean.serializer())
    val BOUNTY_OBJS by "objects".forComponent(ListSerializer(BountyDataEntry.serializer()), JsonFormats.MojangSerializer)
    val BOUNTY_REWS by "rewards".forComponent(ListSerializer(BountyDataEntry.serializer()), JsonFormats.MojangSerializer)
    val BOUNTY_COMPLETION by "completion".forComponent(BountyCompletion.serializer())
    val DECREE_DATA by "decree_data".forComponent(DecreeData.serializer())

    object CustomStats {
        private val simpleFormat = StatFormatter { "$it" }
        val BOUNTIES_TAKEN by "bounties_taken".forStat(simpleFormat)
        val BOUNTIES_COMPLETED by "bounties_done".forStat(simpleFormat)
        val BOUNTY_COMPLETION_TIME by "bounty_completion_time".forStat(StatFormatter.TIME)
    }

    object Triggers {
        val BOUNTY_COMPLETED by "bounty_completed".forCriterionTrigger { SimpleCriterion() }
        val RUSH_ORDER by "rush_order".forCriterionTrigger { SimpleCriterion() }
        val PROCRASTINATOR by "procrastinator".forCriterionTrigger { SimpleCriterion() }
        val FETCH_QUEST by "fetch_quest".forCriterionTrigger { SimpleCriterion() }
        val DECREE_PLACED by "decree_placed".forCriterionTrigger { SimpleCriterion() }
        val ALL_DECREES_PLACED by "all_decrees_placed".forCriterionTrigger { SimpleCriterion() }
        val PRINTING_PRESS by "printing_press".forCriterionTrigger { SimpleCriterion() }
    }


    init {
        CustomStats // yep
        Triggers
    }

    private fun String.forVillagerPoi(memModule: Lazy<MemoryModuleType<GlobalPos>>, stateSet: Set<BlockState>, tickets: Int, searchDistance: Int): ResourceKey<PoiType>? {
        val registryKey = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Bountiful.id(this))
        val poiMap = Villager.POI_MEMORIES.toMutableMap()
        val bio: BiPredicate<Villager, Holder<PoiType>> = BiPredicate { vill, poiType ->
            poiType.`is`(registryKey)
        }
        poiMap[memModule.value] = bio
        // The following two lines need an AW/AT
        Villager.POI_MEMORIES = poiMap
        PoiTypes.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, registryKey, stateSet, tickets, searchDistance)
        return registryKey
    }
//
//    private fun String.forSimplePoi(memModule: Lazy<MemoryModuleType<GlobalPos>>)

}