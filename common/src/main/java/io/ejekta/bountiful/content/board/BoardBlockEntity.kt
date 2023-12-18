package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulTriggers
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import io.ejekta.bountiful.util.checkOnBoard
import io.ejekta.bountiful.util.hackyGiveTradeExperience
import io.ejekta.bountiful.util.readOnlyCopy
import io.ejekta.bountiful.util.weightedRandomIntBy
import io.ejekta.kambrik.ext.ksx.decodeFromStringTag
import io.ejekta.kambrik.ext.ksx.encodeToStringTag
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.poi.PointOfInterestStorage
import net.minecraft.world.poi.PointOfInterestType
import java.util.*
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull


class BoardBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BountifulContent.BOARD_ENTITY, pos, state), NamedScreenHandlerFactory {

    private val decrees = SimpleInventory(3)
    private val bounties = BountyInventory()

    private var boardUUID = UUID.randomUUID()

    private var takenMask = mutableMapOf<String, MutableSet<Int>>()
    private val takenSerializer = MapSerializer(String.serializer(), SetSerializer(Int.serializer()))

    // Only need to calc this once per object, I don't see it changing often
    private val villageTag = Registries.POINT_OF_INTEREST_TYPE.streamTags().filter { it.id == Identifier("village") }.findFirst().getOrNull()

    fun maskFor(player: PlayerEntity): MutableSet<Int> {
        return takenMask.getOrPut(player.uuidAsString) { mutableSetOf() }
    }

    private fun clearMask(slot: Int) {
        // Clear mask because slot was updated
        takenMask.forEach { (_, mask) ->
            mask.removeIf { it == slot }
        }
    }

    // Slot #, Age
    private var bountyTimestamps = mutableMapOf<Int, Long>()
    private val bountyStampSerializer = MapSerializer(Int.serializer(), Long.serializer())

    private var finishMap = mutableMapOf<String, Int>()
    private val finishSerializer = MapSerializer(String.serializer(), Int.serializer())

    // Whether this board has even been initialized/given starting data
    private val isPristine: Boolean
        get() = bounties.isEmpty && finishMap.keys.isEmpty() && takenMask.keys.isEmpty()

    // Calculated level, progress to next, point of next level
    private val levelData: Triple<Int, Int, Int>
        get() = levelProgress(finishMap.values.sum())

    private val reputation: Int
        get() = levelData.first

    private val takenSlots: Set<Int>
        get() = BountyInventory.bountySlots.filter { !bounties.getStack(it).isEmpty }.toSet()

    private val freeSlots: Set<Int>
        get() = BountyInventory.bountySlots.toSet() - takenSlots

    private fun weightedBountySlot(): Int {
        val worldTime = world?.time ?: return -1
        return takenSlots.toList().weightedRandomIntBy {
            val putOnBoard = bountyTimestamps[this] ?: 0L
            (worldTime - putOnBoard).toInt() // this will be a problem if a bounty is left on the board for over 3.4 years (lol)
        }
    }

    // Holds pickups for villagers, key is profession and value are items to pick up
    private val villagerPickups = mutableMapOf<String, MutableSet<ItemStack>>()

    val numCompleted: Int
        get() = finishMap.values.sum()

    private fun incrementCompletedBounties(player: PlayerEntity) {
        finishMap[player.uuidAsString] = finishMap.getOrPut(player.uuidAsString) { 0 } + 1
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

    fun updateUponBountyCompletion(player: ServerPlayerEntity, bountyData: BountyData) {
        // Award advancement to player
        BountifulTriggers.BOUNTY_COMPLETED.trigger(player)
        // Tick completion upwards
        incrementCompletedBounties(player)
        // Fill pickups
        villagerPickupPopulate(bountyData)
        // Have a villager check on the board
        getBestVillager(bountyData)?.checkOnBoard(pos)
    }

    private fun addBountyToRandomSlot(stack: ItemStack) {
        val slotNum = freeSlots.randomOrNull()
        slotNum?.let {
            addBounty(it, stack)
        }
    }

    private fun randomlyPruneOldBounty() {
        val slotNum = weightedBountySlot()
        if (slotNum in BountyInventory.bountySlots) {
            removeBounty(slotNum)
        }
    }

    private fun addBounty(slot: Int, stack: ItemStack) {
        Bountiful.LOGGER.debug("Adding bounty to slot $slot")
        if (slot !in BountyInventory.bountySlots) return

        // Update timestamps
        world?.time?.let { bountyTimestamps[slot] = it }

        modifyTrackedGuiInvs {
            it.setStack(slot, stack.copy()) // All connected players get copies, so that taken bounties are instanced
        }
        clearMask(slot)
        bounties.setStack(slot, stack)
    }

    fun removeBounty(slot: Int) {
        Bountiful.LOGGER.debug("Removing bounty from slot $slot")
        modifyTrackedGuiInvs {
            it.removeStack(slot)
        }
        clearMask(slot)
        bounties.removeStack(slot)
    }

    // If the bounty board has never been used before (pristine), populate it
    fun tryInitialPopulation() {
        if (isPristine) {
            if (decrees.isEmpty) {
                decrees.setStack((0..2).random(), DecreeItem.create(
                    DecreeSpawnCondition.BOARD_SPAWN, 1, DecreeSpawnRank.CONSTANT
                ))
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

        val makeBounty: () -> ItemStack = {
            BountyCreator.createBountyItem(
                ourWorld,
                pos,
                getBoardDecrees(),
                levelData.first.coerceIn(-30..30),
                ourWorld.time
            )
        }

        //println("Free slots: $freeSlots")

        val chance = takenSlots.size - freeSlots.size
        // If there's more taken than free, prune
        if (takenSlots.size >= 12) {
            val randomNumPrunes = listOf(1, 1, 1, 1, 2, 2, 2).random()
            (0 until randomNumPrunes).forEach { _ ->
                randomlyPruneOldBounty()
            }
        }

        if (freeSlots.size > 1) {
            addBountyToRandomSlot(makeBounty())
            if (freeSlots.size >= 18) {
                addBountyToRandomSlot(makeBounty())
            }
        }

        markDirty()
    }

    fun fullInventoryCopy(): BoardInventory {
        return BoardInventory(pos, bounties.clone(), decrees)
    }

    private fun getMaskedInventory(player: PlayerEntity): BoardInventory {
        return BoardInventory(pos, bounties.cloned(maskFor(player)), decrees)
    }

    // Sync properties to show server values to client

    private val DoneProperty = object : PropertyDelegate {
        override fun get(index: Int) = numCompleted
        override fun set(index: Int, value: Int) {  }
        override fun size() = 1
    }

    // Serialization

    override fun readNbt(base: NbtCompound) {
        val decreeList = base.getCompound("decree_inv") ?: return
        val bountyList = base.getCompound("bounty_inv") ?: return

        boardUUID = UUID.fromString(base.getString("boardId"))

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

        val timeStampMap = base.get("timestamps")
        if (timeStampMap != null) {
            bountyTimestamps = JsonFormats.Hand.decodeFromStringTag(bountyStampSerializer, timeStampMap as NbtString).toMutableMap()
        }

        val takenData = base.get("taken")
        if (takenData != null) {
            takenMask = JsonFormats.Hand.decodeFromStringTag(takenSerializer, takenData as NbtString).map {
                it.key to it.value.toMutableSet()
            }.toMap().toMutableMap()
        }
    }

    override fun writeNbt(base: NbtCompound) {
        super.writeNbt(base)

        base.putString("boardId", boardUUID.toString())

        val doneMap = JsonFormats.Hand.encodeToStringTag(finishSerializer, finishMap)
        base.put("completed", doneMap)

        val timeStampMap = JsonFormats.Hand.encodeToStringTag(bountyStampSerializer, bountyTimestamps)
        base.put("timestamps", timeStampMap)

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

    // Villager & Completion Logic

    private fun villagerPickupPopulate(bountyData: BountyData) {
        val stackMap = bountyData.objectives.filter { it.logic is BountyTypeItem }.map {
            BountyTypeItem.getItemStack(it) to it.getRelatedProfessions()
        }
        for ((stack, profs) in stackMap) {
            for (prof in profs) {
                val stackSet = villagerPickups.getOrPut(prof) { mutableSetOf() }
                stackSet.add(stack)
            }
        }
    }

    private fun villagerDoPickup(villagerEntity: VillagerEntity) {
        val prof = villagerEntity.villagerData.profession.id
        val stackSet = villagerPickups.getOrPut(prof) { mutableSetOf() }
        // Try pull from matching profession bucket
        if (stackSet.isNotEmpty()) {
            // Pulling from profession completion
            val toUse = stackSet.toList().shuffled().first()
            villagerEntity.equipStack(EquipmentSlot.MAINHAND, toUse)
            stackSet.clear()

            villagerRewardForPickup(villagerEntity, (reputation / 5) + 1, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES)
        } else {
            val randomProfSet = villagerPickups.keys.randomOrNull()
            if (randomProfSet != null && randomProfSet in villagerPickups.keys) {
                // Pulling from random completion
                val newStackSet = villagerPickups[randomProfSet] ?: return
                val newToUse = newStackSet.toList().shuffled().firstOrNull()
                newToUse?.let {
                    villagerEntity.equipStack(EquipmentSlot.MAINHAND, it)
                }
                newStackSet.clear()
            }
            villagerRewardForPickup(villagerEntity, 1, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES)
        }
    }

    // Reward a villager for pickup
    private fun villagerRewardForPickup(villagerEntity: VillagerEntity, exp: Int, status: Byte) {
        (villagerEntity.world as? ServerWorld)?.sendEntityStatus(villagerEntity, status)
        villagerEntity.hackyGiveTradeExperience(exp)
        villagerEntity.restock()
    }

    private fun findNearestVillagers(range: Int): List<VillagerEntity> {
        return world?.getEntitiesByClass(
            VillagerEntity::class.java,
            Box.of(pos.toCenterPos(), range * 1.0, range * 1.0, range * 1.0)
        ) { true } ?: emptyList()
    }

    // This may show false on clients because no serverworld for POIs, this should perhaps be a Property that gets sent instead
    private fun isNearVillage(): Boolean {
        val serverWorld = world as? ServerWorld ?: return false

        val rep = Predicate<RegistryEntry<PointOfInterestType>> {
            villageTag != null && it.isIn(villageTag)
        }

        val result = serverWorld.pointOfInterestStorage.getNearestTypeAndPosition(rep, pos, 256,
            PointOfInterestStorage.OccupationStatus.ANY
        ).getOrNull()

        result?.let {
            return it.second.getManhattanDistance(pos) < 128
        }
        return false
    }

    private fun getBestVillager(bountyData: BountyData): VillagerEntity? {
        val nearestVillagers = findNearestVillagers(64)
        if (nearestVillagers.isEmpty()) {
            return null
        }

        val villagerProfessions = nearestVillagers.map { it.villagerData.profession.id }.toSet()

        val matchingProfs = bountyData.objectives.filter {
            it.getRelatedProfessions().intersect(villagerProfessions).isNotEmpty()
        }

        val villager = if (matchingProfs.isEmpty()) {
            // No matching profession, picking a random villager
            nearestVillagers
        } else {
            // Matching professions, picking an entry we can use!
            val randomObj = matchingProfs.random()
            nearestVillagers.filter {
                it.villagerData.profession.id in randomObj.getRelatedProfessions()
            }
        }.random()

        return villager
    }

    fun handleVillagerVisit(villagerEntity: VillagerEntity) {
        //println("A villager is visiting the Bounty Board!")
        val serverWorld = world as? ServerWorld ?: return
        villagerDoPickup(villagerEntity)
        serverWorld.playSound(villagerEntity, villagerEntity.blockPos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f)
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
                    // Get revealable decrees
                    val revealable = BountifulContent.Decrees.filter(DecreeSpawnCondition.BOARD_REVEAL.spawnFunc).map { it.id }
                    DecreeData.edit(stack) {
                        if (ids.isEmpty()) {
                            // Random populate
                            DecreeSpawnRank.RANDOM.populateFunc(this, revealable)
                        }
                    }
                }
            }

            // Add & remove bounties according to update frequency
            if ((world.time + 13L) % (20L * BountifulIO.configData.board.updateFrequency) == 0L) {
                // Change bounty population
                entity.randomlyUpdateBoard()
            }


            // Remove expired bounties every 100 ticks
            if (world.time % 100L == 4L) {
                for (i in 0 until entity.bounties.size()) {
                    val stack = entity.bounties.getStack(i)
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