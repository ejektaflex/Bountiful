package io.ejekta.bountiful.content

import com.mojang.serialization.Codec
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
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

    val BOUNTY_ITEM = "bounty" forItem BountyItem()

    val DECREE_ITEM = "decree" forItem DecreeItem()

    val BOARD = "bountyboard" forBlock BoardBlock()

    val BOARD_ITEM = "bountyboard" forItem BlockItem(BOARD, Item.Settings().maxCount(1).fireproof())

    val BOARD_ENTITY = "board-be".forBlockEntity(BOARD, ::BoardBlockEntity)

    val BOARD_SCREEN_HANDLER = "board" forScreen ::BoardScreenHandler

    val MEM_MODULE_NEAREST_BOARD = "nearest_bounty_board".forRegistration(
        Registries.MEMORY_MODULE_TYPE,
        MemoryModuleType(Optional.empty<Codec<GlobalPos>>())
    ) as MemoryModuleType<GlobalPos>

    val POI_BOUNTY_BOARD: RegistryKey<PointOfInterestType> = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Bountiful.id("bountyboard"))

    init {
        val abc = VillagerEntity.POINTS_OF_INTEREST.toMutableMap()
        val bio: BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>> = BiPredicate { vill, poiEntry ->
            poiEntry.matchesKey(POI_BOUNTY_BOARD)
        }
        abc[MEM_MODULE_NEAREST_BOARD] = bio
        VillagerEntity.POINTS_OF_INTEREST = abc

        PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, POI_BOUNTY_BOARD, setOf(BOARD.defaultState), 1, 1)
    }

}