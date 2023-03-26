package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.bountiful.content.DecreeItem
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.util.readOnlyCopy
import io.ejekta.kambrik.ext.ksx.decodeFromStringTag
import io.ejekta.kambrik.ext.ksx.encodeToStringTag
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World


class BoardBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(BountifulContent.BOARD_ENTITY, pos, state),
    NamedScreenHandlerFactory {

    private val decrees = SimpleInventory(3)
    private val bounties = BountyInventory()

    private var takenMask = mutableMapOf<String, MutableSet<Int>>()
    private val takenSerializer = MapSerializer(String.serializer(), SetSerializer(Int.serializer()))

    fun maskFor(player: PlayerEntity): MutableSet<Int> {
        return takenMask.getOrPut(player.uuidAsString) { mutableSetOf() }
    }

    private var finishMap = mutableMapOf<String, Int>()
    private val finishSerializer = MapSerializer(String.serializer(), Int.serializer())

    // Whether this board has even been initialized/given starting data
    private val isPristine: Boolean
        get() = bounties.isEmpty && finishMap.keys.isEmpty() && takenMask.keys.isEmpty()

    // Calculated level, progress to next, point of next level
    private val levelData: Triple<Int, Int, Int>
        get() = levelProgress(finishMap.values.sum())

    private fun setDecree() {
        if (world is ServerWorld && decrees.isEmpty) {
            val slot = (0..2).random()
            val stack = DecreeItem.create()
            decrees.setStack(
                slot,
                stack
            )
        }
    }

    val numCompleted: Int
        get() = finishMap.values.sum()

    fun updateCompletedBounties(player: PlayerEntity) {
        finishMap[player.uuidAsString] = finishMap.getOrPut(player.uuidAsString) {
            0
        } + 1
    }

    private fun getBoardDecrees(): Set<Decree> {
        return BountifulContent.getDecrees(
            decrees.readOnlyCopy.filter {
                it.item is DecreeItem && it.count > 0
            }.map {
                DecreeData[it].ids
            }.flatten().toSet()
        )
    }

    private fun getPlayersTrackingUs(): List<ServerPlayerEntity> {
        return (world as? ServerWorld)?.chunkManager?.threadedAnvilChunkStorage?.getPlayersWatchingChunk(ChunkPos(pos)) ?: emptyList()
    }

    private fun modifyTrackedGuiInvs(func: (inv: BoardInventory) -> Unit) {
        val players = getPlayersTrackingUs()
        players.forEach { player ->
            val handler = player.currentScreenHandler as? BoardScreenHandler
            // The handler has to refer to the same Board position as the Entity
            if (handler?.inventory?.pos == pos) {
                handler?.let {
                    val boardInv = it.inventory
                    func(boardInv)
                }
            }
        }
    }

    private fun addBounty(slot: Int, stack: ItemStack) {
        if (slot !in BountyInventory.bountySlots) return

        modifyTrackedGuiInvs {
            it.setStack(slot, stack.copy()) // All connected players get copies, so that taken bounties are instanced
        }

        bounties.setStack(slot, stack)
    }

    fun removeBounty(slot: Int) {
        modifyTrackedGuiInvs {
            it.removeStack(slot)
        }

        bounties.removeStack(slot)
    }

    // If the bounty board has never been used before (pristine), populate it
    fun tryInitialPopulation() {
        if (isPristine) {
            if (decrees.isEmpty) {
                decrees.setStack((0..2).random(), DecreeItem.create())
            }
            for (i in 0..5) {
                randomlyUpdateBoard()
            }
        }
        markDirty()
    }

    private fun randomlyUpdateBoard() {
        val ourWorld = world as? ServerWorld ?: return
        if (decrees.isEmpty) {
            return
        }

        val slotToAddTo = BountyInventory.bountySlots.random()

        // ~42% to remove none, ~28% to remove 1, ~28% to remove 2
        val slotsToRemove = (0 until listOf(0, 0, 0, 1, 1, 2, 2).random()).map {
            (BountyInventory.bountySlots - slotToAddTo).random()
        }

        val commonBounty = BountyCreator.createBountyItem(
            ourWorld,
            pos,
            getBoardDecrees(),
            levelData.first.coerceIn(-30..30),
            ourWorld.time
        )

        // Add to board
        removeBounty(slotToAddTo)

        if (commonBounty != null) {
            addBounty(slotToAddTo, commonBounty)
        } else {
            Bountiful.LOGGER.warn("Cannot create a bounty for board with these decrees: ${getBoardDecrees().map { it.id }}")
        }


        // Clear mask because slot was updated
        takenMask.forEach { (uuid, mask) ->
            mask.removeIf { it == slotToAddTo || it in slotsToRemove }
        }

        // Remove from board
        slotsToRemove.forEach { i ->
            removeBounty(i)
        }

        markDirty()
    }

    fun fullInventoryCopy(): BoardInventory {
        return BoardInventory(pos, bounties.clone(), decrees)
    }

    private fun getMaskedInventory(player: PlayerEntity): BoardInventory {
        return BoardInventory(pos, bounties.cloned(maskFor(player)), decrees)
    }



    val DoneProperty = object : PropertyDelegate {
        override fun get(index: Int) = numCompleted
        override fun set(index: Int, value: Int) {  }
        override fun size() = 1
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun readNbt(base: NbtCompound) {
        val decreeList = base.getCompound("decree_inv") ?: return
        val bountyList = base.getCompound("bounty_inv") ?: return

        Inventories.readNbt(
            decreeList,
            decrees.stacks
        )

        Inventories.readNbt(
            bountyList,
            bounties.stacks
        )

        val doneMap = base.get("completed")
        //println("Done map is: $doneMap")
        if (doneMap != null) {
            finishMap = JsonFormats.Hand.decodeFromStringTag(finishSerializer, doneMap as NbtString).toMutableMap()
        }

        val takenData = base.get("taken")
        if (takenData != null) {
            takenMask = JsonFormats.Hand.decodeFromStringTag(takenSerializer, takenData as NbtString).map {
                it.key to it.value.toMutableSet()
            }.toMap().toMutableMap()
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun writeNbt(base: NbtCompound) {
        super.writeNbt(base)


        val doneMap = JsonFormats.Hand.encodeToStringTag(finishSerializer, finishMap)
        base.put("completed", doneMap)

        base.put(
            "taken",
            JsonFormats.Hand.encodeToStringTag(takenSerializer, takenMask)
        )

        val decreeList = NbtCompound()
        Inventories.writeNbt(decreeList, decrees.readOnlyCopy)

        val bountyList = NbtCompound()
        Inventories.writeNbt(bountyList, bounties.readOnlyCopy)

        base.put("decree_inv", decreeList)
        base.put("bounty_inv", bountyList)
    }

    companion object {

        fun levelProgress(done: Int, per: Int = 2): Triple<Int, Int, Int> {
            var doneAcc = done
            var perAcc = per
            var levels = 0

            while (doneAcc >= perAcc * 5) {
                levels += 5
                doneAcc -= perAcc * 5
                perAcc += 1
            }

            levels += doneAcc / perAcc
            return Triple(levels, doneAcc % perAcc, perAcc)
        }

        @JvmStatic
        fun tick(world: World, pos: BlockPos, state: BlockState, entity: BoardBlockEntity) {
            if (world.isClient) return

            entity.tryInitialPopulation()

            // Set unset decrees every 20 ticks
            if (world.time % 20L == 0L) {
                entity.decrees.stacks.filter {
                    it.item is DecreeItem // must be a decree and not null
                }.forEach { stack ->
                    DecreeData.edit(stack) {
                        if (ids.isEmpty() && BountifulContent.Decrees.isNotEmpty()) {
                            ids.add(BountifulContent.Decrees.random().id)
                        }
                    }
                }
            }

            // Add & remove bounties according to update frequency
            if ((world.time + 13L) % (20L * BountifulIO.configData.boardUpdateFrequency) == 0L) {
                // Change bounty population
                entity.randomlyUpdateBoard()
            }


            // Remove expired bounties every 100 ticks
            if (world.time % 100L == 4L) {
                for (i in 0 until entity.bounties.size()) {
                    var stack = entity.bounties.getStack(i)
                    if (stack.item !is BountyItem) {
                        continue
                    }
                    val info = BountyInfo[stack]
                    if (info.timeLeft(world) <= 0) {
                        entity.removeBounty(i)
                    }
                }
            }

        }

    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return BoardScreenHandler(syncId, playerInventory, getMaskedInventory(player), DoneProperty)
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
    }

}