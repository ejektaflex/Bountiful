package io.ejekta.bountiful.content

import com.mojang.serialization.Codec
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.advancement.SimpleCriterion
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.block.BlockState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.stat.Stat
import net.minecraft.stat.StatFormatter
import net.minecraft.stat.Stats
import net.minecraft.util.math.GlobalPos
import net.minecraft.world.poi.PointOfInterestType
import net.minecraft.world.poi.PointOfInterestTypes
import java.util.*
import java.util.function.BiPredicate

object BountifulContent : KambrikAutoRegistrar {

    override fun getId() = "bountiful"

    val Decrees = mutableListOf<Decree>()

    val Pools = mutableListOf<Pool>()

    fun getDecrees(ids: Set<String>): Set<Decree> {
        return ids.mapNotNull { id ->
            Decrees.find { it.id == id }
        }.toSet()
    }

    val BOUNTY_ITEM by "bounty" forItem { BountyItem() }

    val DECREE_ITEM by "decree" forItem { DecreeItem() }

    val BOARD = "bountyboard" forBlock { BoardBlock() }

    val BOARD_ITEM by "bountyboard" forItem { BlockItem(BOARD.value, Item.Settings().maxCount(1).fireproof()) }

    val BOARD_ENTITY by "board-be".forBlockEntity(BOARD, ::BoardBlockEntity)

    val BOARD_SCREEN_HANDLER by "board" forScreen ::BoardScreenHandler

    val ANALYZER_SCREEN_HANDLER by "analyzer" forScreen ::AnalyzerScreenHandler

    val MEM_MODULE_NEAREST_BOARD_INSTANCE = "nearest_bounty_board".forRegistration(
        Registries.MEMORY_MODULE_TYPE
    ) { MemoryModuleType(Optional.empty<Codec<GlobalPos>>()) } as Lazy<MemoryModuleType<GlobalPos>>

    val MEM_MODULE_NEAREST_BOARD by MEM_MODULE_NEAREST_BOARD_INSTANCE

    //val POI_BOUNTY_BOARD = "bountyboard".forVillagerPoi(MEM_MODULE_NEAREST_BOARD_INSTANCE, setOf(BOARD.value.defaultState), 1, 1)

    object CustomStats {
        private val simpleFormat = StatFormatter { "$it" }
        val BOUNTIES_TAKEN by "bounties_taken".forStat(simpleFormat)
        val BOUNTIES_COMPLETED by "bounties_done".forStat(simpleFormat)
        val BOUNTY_COMPLETION_TIME by "bounty_completion_time".forStat(StatFormatter.TIME)
    }

    object Triggers {
        val BOUNTY_COMPLETED by "bounty_completed".forCriterion { SimpleCriterion() }
        val RUSH_ORDER by "rush_order".forCriterion { SimpleCriterion() }
        val PROCRASTINATOR by "procrastinator".forCriterion { SimpleCriterion() }
        val FETCH_QUEST by "fetch_quest".forCriterion { SimpleCriterion() }
        val DECREE_PLACED by "decree_placed".forCriterion { SimpleCriterion() }
        val ALL_DECREES_PLACED by "all_decrees_placed".forCriterion { SimpleCriterion() }
        val PRINTING_PRESS by "printing_press".forCriterion { SimpleCriterion() }
    }


    init {
        CustomStats // yep
        Triggers
    }

//    private fun String.forVillagerPoi(memModule: Lazy<MemoryModuleType<GlobalPos>>, stateSet: Set<BlockState>, tickets: Int, searchDistance: Int): RegistryKey<PointOfInterestType>? {
//        val registryKey = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Bountiful.id(this))
//        val poiMap = VillagerEntity.POINTS_OF_INTEREST.toMutableMap()
//        val bio: BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>> = BiPredicate { vill, poiEntry ->
//            poiEntry.matchesKey(registryKey)
//        }
//        poiMap[memModule.value] = bio
//        VillagerEntity.POINTS_OF_INTEREST = poiMap
//        PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, registryKey, stateSet, tickets, searchDistance)
//        return registryKey
//    }
//
//    private fun String.forSimplePoi(memModule: Lazy<MemoryModuleType<GlobalPos>>)

}