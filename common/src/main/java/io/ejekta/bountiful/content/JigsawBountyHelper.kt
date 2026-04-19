package io.ejekta.bountiful.content

import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.mixin.JigsawPlacerAccessor
import io.ejekta.bountiful.mixin.SinglePoolElementAccessor
import net.minecraft.resources.Identifier
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import java.util.Random

object JigsawBountyHelper {

    private val BOUNTY_GAZEBO_ID = Identifier.fromNamespaceAndPath("bountiful", "village/common/bounty_gazebo")

    // null = not inside a village generation call on this thread
    private val allowedBoards: ThreadLocal<Int?> = ThreadLocal.withInitial { null }

    fun onAddPiecesStart(centerPiece: PoolElementStructurePiece) {
        val freq = BountifulIO.configData.board.villageGenFrequency
        val guaranteed = freq.toInt()
        val extraChance = freq - guaranteed
        val bb = centerPiece.boundingBox
        // Seeded by village position + a Bountiful-specific salt, independent of world gen RNG
        val seed = bb.minX().toLong() * 341873128712L + bb.minZ().toLong() * 132897987541L xor 0xB0_4B_04_4BL
        val extra = if (Random(seed).nextFloat() < extraChance) 1 else 0
        allowedBoards.set(guaranteed + extra)
    }

    fun onAddPiecesEnd() {
        allowedBoards.remove()
    }

    private fun isBountyBoardElement(element: StructurePoolElement): Boolean {
        if (element !is SinglePoolElement) return false
        val accessor = element as SinglePoolElementAccessor
        val id: Identifier = accessor.bountiful_getTemplate().left().orElse(null) ?: return false
        return id == BOUNTY_GAZEBO_ID
    }

    private fun isBountyBoardPiece(piece: PoolElementStructurePiece): Boolean =
        isBountyBoardElement(piece.element)

    fun filterTargetPieces(
        targetPieces: List<StructurePoolElement>,
        placer: JigsawPlacerAccessor
    ): List<StructurePoolElement> {
        val boards = targetPieces.filter { isBountyBoardElement(it) }
        if (boards.isEmpty()) return targetPieces

        val allowed = allowedBoards.get() ?: 0
        val alreadyPlaced = placer.bountiful_getPieces().count { isBountyBoardPiece(it) }

        return if (alreadyPlaced >= allowed) {
            targetPieces.filter { !isBountyBoardElement(it) }
        } else {
            // Move board to front so it is tried before any other house piece
            boards + targetPieces.filter { !isBountyBoardElement(it) }
        }
    }
}
