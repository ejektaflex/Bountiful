package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.components.*
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.decree.DecreeSpawnRank
import io.ejekta.bountiful.util.*
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull


class BoardBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BountifulContent.BOARD_ENTITY, pos, state), MenuProvider {

    // Per-board state used in non-global mode. In global mode, the server-wide GlobalBoardData is used instead.
    private var localState = GlobalBoardData()

    private val isGlobalMode: Boolean
        get() = BountifulIO.configData.board.globalBoardState

    private val globalData: GlobalBoardData?
        get() = if (isGlobalMode) serverWorld?.server?.dataStorage?.computeIfAbsent(GlobalBoardData.TYPE) else null

    private val activeState: GlobalBoardData
        get() = globalData ?: localState

    // Only need to calc this once per object, I don't see it changing often
    private val villageTag = TagKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), Identifier.parse("village"))

    private operator fun get(player: Player): GlobalBoardData.PlayerBoardData {
        return activeState.playerData.getOrPut(player.stringUUID) { GlobalBoardData.PlayerBoardData.empty() }
    }

    fun maskFor(player: Player): MutableSet<Int> {
        return this[player].taken
    }

    private fun clearMask(slot: Int) {
        // Clear mask because slot was updated
        activeState.playerData.forEach { (_, data) ->
            data.taken.removeIf { it == slot }
        }
    }

    // Whether this board has even been initialized/given starting data
    private val isPristine: Boolean
        get() = activeState.decrees.isEmpty && activeState.bounties.isEmpty && activeState.playerData.isEmpty()

    // Calculated level, progress to next, point of next level
    private val levelData: Triple<Int, Int, Int>
        get() = levelProgress(activeState.playerData.values.sumOf { it.done })

    private val reputation: Int
        get() = levelData.first

    private val serverWorld: ServerLevel?
        get() = level as? ServerLevel

    private var activeLastUpdatedTime: Long
        get() = activeState.lastUpdatedTime
        set(value) {
            activeState.lastUpdatedTime = value
            markStateDirty()
        }

    private val takenSlots: Set<Int>
        get() = BoardInventory.BOUNTY_RANGE.filter { !activeState.bounties.getItem(it).isEmpty }.toSet()

    private val freeSlots: Set<Int>
        get() = BoardInventory.BOUNTY_RANGE.toSet() - takenSlots

    private fun weightedBountySlot(): Int {
        val worldTime = level?.gameTime ?: return -1
        return takenSlots.toList().weightedRandomIntBy {
            val putOnBoard = activeState.bountyTimestamps[this] ?: 0L
            (worldTime - putOnBoard).toInt() // this will be a problem if a bounty is left on the board for over 3.4 years (lol)
        }
    }

    // Holds pickups for villagers, key is profession and value are items to pick up
    private val villagerPickups = mutableMapOf<String, MutableSet<ItemStack>>()

    val numCompleted: Int
        get() = activeState.playerData.values.sumOf { it.done }

    private fun markStateDirty() {
        globalData?.setDirty()
        setChanged()
    }

    private fun incrementCompletedBounties(player: Player, timeTakenTicks: Long) {
        activeState.playerData.getOrPut(player.stringUUID) { GlobalBoardData.PlayerBoardData.empty() }.apply {
            done += 1
            totalTime += timeTakenTicks
        }
        markStateDirty()
    }

    private fun getBoardDecrees(): Set<Decree> {
        return BountifulContent.getDecrees(
            activeState.decrees.readOnlyCopy.filter {
                it.item is DecreeItem && it.count > 0
            }.map {
                it[BountifulContent.DECREE_DATA]?.ids ?: emptySet()
            }.flatten().toSet()
        )
    }

    private fun getPlayersTrackingUs(): List<ServerPlayer> {
        return (level as? ServerLevel)?.chunkSource?.chunkMap?.getPlayers(ChunkPos.containing(blockPos), false).orEmpty()
    }

    private fun modifyTrackedGuiInvs(func: (inv: BoardInventory) -> Unit) {
        val players = getPlayersTrackingUs()
        players.forEach { player ->
            val handler = player.containerMenu as? BoardScreenHandler
            // The handler has to refer to the same Board position as the Entity
            if (handler?.container?.pos == blockPos) {
                handler?.let {
                    val boardInv = it.container
                    func(boardInv)
                }
            }
        }
    }

    fun updateUponBountyCompletion(player: ServerPlayer, holding: BountyStack) {
        // Award advancement to player
        BountifulContent.Triggers.BOUNTY_COMPLETED.trigger(player)

        player.awardStat(BountifulContent.CustomStats.BOUNTY_COMPLETION_TIME, holding.info.timeTakenTicks(player.level()).toInt())

        // Nightly message
        if (Bountiful.nightly) {
            player.sendSystemMessage(
                Component.translatable("bountiful.nightly.warning")
                    .withStyle(ChatFormatting.GOLD)
            )
        }

        val level = player.level()
        val timeTaken = holding.info.timeTakenTicks(level)

        level.let {
            if (holding.info.timeTakenSecs(it) <= 60) {
                BountifulContent.Triggers.RUSH_ORDER.trigger(player)

            }
            if (holding.info.timeLeftSecs(it) <= 10) {
                BountifulContent.Triggers.PROCRASTINATOR.trigger(player)
            }
            player.awardStat(BountifulContent.CustomStats.BOUNTIES_COMPLETED)
        }

        // Tick completion upwards
        incrementCompletedBounties(player, timeTaken)
        // Fill pickups
        villagerPickupPopulate(holding.objs)
        // Have a villager check on the board
        getBestVillager(holding.objs)?.checkOnBoard(blockPos)
    }

    private fun addBountyToRandomSlot(stack: ItemStack) {
        val slotNum = freeSlots.randomOrNull()
        slotNum?.let {
            addBounty(it, stack)
        }
    }

    private fun randomlyPruneOldBounty() {
        val slotNum = weightedBountySlot()
        if (slotNum in BoardInventory.BOUNTY_RANGE) {
            removeBounty(slotNum)
        }
    }

    private fun addBounty(slot: Int, stack: ItemStack) {
        if (slot !in BoardInventory.BOUNTY_RANGE) return

        // Update timestamps
        level?.gameTime?.let { activeState.bountyTimestamps[slot] = it }

        modifyTrackedGuiInvs {
            it.setItem(slot, stack.copy()) // All connected players get copies, so that taken bounties are instanced
        }
        clearMask(slot)
        activeState.bounties.setItem(slot, stack)
        markStateDirty()
    }

    private fun removeBounty(slot: Int) {
        modifyTrackedGuiInvs {
            it.removeItemNoUpdate(slot)
        }
        clearMask(slot)
        activeState.bounties.removeItemNoUpdate(slot)
        markStateDirty()
    }

    // If the bounty board has never been used before (pristine), populate it
    fun upkeepTryInitialPopulation() {
        if (isPristine) {
            if (activeState.decrees.isEmpty) {
                activeState.decrees.setItem((0..2).random(), DecreeItem.create(
                    DecreeSpawnCondition.BOARD_SPAWN, 1, DecreeSpawnRank.CONSTANT
                ))
            }
            repeat(5) {
                randomlyUpdateBoard()
            }
        }
        markStateDirty()
    }

    // Set unset decrees
    private fun upkeepRevealDecrees() {
        activeState.decrees.items.filter {
            it.item is DecreeItem // must be a decree and not null
        }.forEach { stack ->
            // Get revealable decrees
            val revealable = BountifulContent.Decrees.filter(DecreeSpawnCondition.BOARD_REVEAL.spawnFunc).map { it.id }
            DecreeData.editOn(stack) {
                if (ids.isEmpty()) {
                    // Random populate
                    DecreeSpawnRank.RANDOM.populateFunc(this, revealable)
                }
            }
        }
    }

    fun onUserPlacedDecree(player: ServerPlayer, decStack: ItemStack) {
        BountifulContent.Triggers.DECREE_PLACED.trigger(player)
        checkUserPlacedAllDecrees(player, decStack)
    }

    private fun checkUserPlacedAllDecrees(player: ServerPlayer, newStack: ItemStack) {
        if (newStack.count == 0) {
            return
        }
        // Data component can be absent on decree stacks created/duplicated out-of-band
        // (e.g. via creative pick-block on a board slot, modded item interactions, or
        // an NBT-cleared stack from another mod). Bail out cleanly rather than NPEing
        // the server thread — see issue #336.
        val decreeData = newStack[BountifulContent.DECREE_DATA] ?: return
        val newDecrees = decreeData.ids
        val decs = getBoardDecrees().map { it.id }.toSet() + newDecrees
        val allDecreesSet = BountifulContent.Decrees.map { it.id }.toSet()
        val allDecrees = decs.intersect(allDecreesSet) == allDecreesSet
        Bountiful.LOGGER.trace(allDecreesSet - decs)
        if (allDecrees) {
            Bountiful.LOGGER.info("User $player placed all possible decrees")
            BountifulContent.Triggers.ALL_DECREES_PLACED.trigger(player)
        }
    }

    // Remove expired bounties
    private fun upkeepRemoveExpiredBounties() {
        serverWorld?.let {
            for (i in 0 until activeState.bounties.containerSize) {
                val stack = activeState.bounties.getItem(i)
                if (stack.item !is BountyItem) {
                    continue
                }
                val info = stack[BountifulContent.BOUNTY_INFO] ?: continue
                if (info.timeLeftTicks(it) <= 0) {
                    removeBounty(i)
                }
            }
        }
    }

    private fun upkeepBountyGeneration() {
        // When packmode is enabled, always update each second
        val updateFrequencyTicks = if (Bountiful.packMode) {
            20
        } else {
            BountifulIO.configData.board.updateFrequencySecs * GameTime.TICK_RATE
        }
        serverWorld?.let { sw ->
            if (sw.gameTime - activeLastUpdatedTime >= updateFrequencyTicks && updateFrequencyTicks > 0) {
                val numUpdates = ((sw.gameTime - activeLastUpdatedTime) / updateFrequencyTicks).coerceAtMost(BoardInventory.BOUNTY_SIZE.toLong())
                // We are updating!
                activeLastUpdatedTime = sw.gameTime
                for (i in 0 until numUpdates) {
                    randomlyUpdateBoard()
                }
            }
        }
    }

    private fun randomlyUpdateBoard() {
        val ourWorld = level as? ServerLevel ?: return
        if (activeState.decrees.isEmpty) {
            return
        }

        val makeBounty: () -> ItemStack = {
            val usableDecrees = getBoardDecrees().let { decSet ->
                if (BountifulIO.configData.bounty.allowDecreeMixing) {
                    decSet
                } else {
                    setOfNotNull(decSet.randomOrNull())
                }
            }
            BountyCreator.createBountyItem(
                ourWorld,
                blockPos,
                usableDecrees,
                levelData.first.coerceIn(-30..30)
            )
        }

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

        markStateDirty()
    }

    fun fullInventoryCopy(): BoardInventory {
        return BoardInventory(blockPos, activeState.bounties.clone(), activeState.decrees)
    }

    private fun getMaskedInventory(player: Player): BoardInventory {
        return BoardInventory(blockPos, activeState.bounties.cloned(maskFor(player)), activeState.decrees)
    }

    // Sync properties to show server values to client

    private val DoneProperty = object : ContainerData {
        override fun get(index: Int) = numCompleted
        override fun set(index: Int, value: Int) {  }
        override fun getCount() = 1
    }

    // Serialization

    override fun loadAdditional(input: ValueInput) {
        localState.loadFrom(input)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (!isGlobalMode) {
            localState.saveTo(output)
        }
    }

    // Villager & Completion Logic

    private fun villagerPickupPopulate(objectives: List<BountyDataEntry>) {
        val stackMap = objectives.filter { it.logic is BountyTypeItem }.mapNotNull { entry ->
            serverWorld?.let {
                BountyTypeItem.getItemStack(entry, it.registryAccess()) to entry.getRelatedProfessions()
            }
        }
        for ((stack, profs) in stackMap) {
            for (prof in profs) {
                val stackSet = villagerPickups.getOrPut(prof) { mutableSetOf() }
                stackSet.add(stack)
            }
        }
    }

    private fun villagerDoPickup(villagerEntity: Villager) {
        val profKey = villagerEntity.villagerData.profession.unwrapKey().map { it.identifier().toString() }.orElse("minecraft:none")
        val stackSet = villagerPickups.getOrPut(profKey) { mutableSetOf() }
        // Try pull from matching profession bucket
        if (stackSet.isNotEmpty()) {
            // Pulling from profession completion
            val toUse = stackSet.toList().shuffled().first()
            villagerEntity.setItemSlot(EquipmentSlot.MAINHAND, toUse)
            stackSet.clear()

            villagerRewardForPickup(villagerEntity, (reputation / 5) + 1, EntityEvent.IN_LOVE_HEARTS)
        } else {
            val randomProfSet = villagerPickups.keys.randomOrNull()
            if (randomProfSet != null && randomProfSet in villagerPickups.keys) {
                // Pulling from random completion
                val newStackSet = villagerPickups[randomProfSet] ?: return
                val newToUse = newStackSet.toList().shuffled().firstOrNull()
                newToUse?.let {
                    villagerEntity.setItemSlot(EquipmentSlot.MAINHAND, it)
                }
                newStackSet.clear()
            }
            villagerRewardForPickup(villagerEntity, 1, EntityEvent.VILLAGER_HAPPY)
        }
    }

    // Reward a villager for pickup
    private fun villagerRewardForPickup(villagerEntity: Villager, exp: Int, status: Byte) {
        (villagerEntity.level() as? ServerLevel)?.broadcastEntityEvent(villagerEntity, status)
        villagerEntity.hackyGiveTradeExperience(exp)
        villagerEntity.restock()
    }

    private fun findNearestVillagers(range: Int): List<Villager> {
        return level?.getEntitiesOfClass(
            Villager::class.java,
            AABB.ofSize(blockPos.center, range * 1.0, range * 1.0, range * 1.0)
        ) { true } ?: emptyList()
    }

    // This may show false on clients because no serverworld for POIs, this should perhaps be a Property that gets sent instead
    private fun isNearVillage(): Boolean {
        val serverWorld = level as? ServerLevel ?: return false

        val rep = Predicate<Holder<PoiType>> {
            villageTag != null && it.`is`(villageTag)
        }

        val result = serverWorld.poiManager.findClosestWithType(rep, blockPos, 256,
            PoiManager.Occupancy.ANY
        ).getOrNull()

        result?.let {
            return it.second.distManhattan(blockPos) < 128
        }
        return false
    }

    private fun getBestVillager(objectives: List<BountyDataEntry>): Villager? {
        val nearestVillagers = findNearestVillagers(64)
        if (nearestVillagers.isEmpty()) {
            return null
        }

        val villagerProfessions = nearestVillagers.mapNotNull {
            it.villagerData.profession.unwrapKey().map { key -> key.identifier().toString() }.orElse(null)
        }.toSet()

        val matchingProfs = objectives.filter {
            it.getRelatedProfessions().intersect(villagerProfessions).isNotEmpty()
        }

        val villager = if (matchingProfs.isEmpty()) {
            // No matching profession, picking a random villager
            nearestVillagers
        } else {
            // Matching professions, picking an entry we can use!
            val randomObj = matchingProfs.random()
            nearestVillagers.filter {
                it.villagerData.profession.unwrapKey().map { key -> key.identifier().toString() }
                    .orElse("") in randomObj.getRelatedProfessions()
            }
        }.random()

        return villager
    }

    fun handleVillagerVisit(villagerEntity: Villager) {
        //println("A villager is visiting the Bounty Board!")
        val serverWorld = level as? ServerLevel ?: return
        villagerDoPickup(villagerEntity)
        serverWorld.playSound(villagerEntity, villagerEntity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1f, 1f)
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
        fun tick(world: Level, pos: BlockPos, state: BlockState, entity: BoardBlockEntity) {
            if (world.isClientSide) return

            entity.upkeepTryInitialPopulation()

            world.everySeconds(1) {
                entity.upkeepRevealDecrees()
            }

            world.everySeconds(1, offset = 1) {
                entity.upkeepBountyGeneration()
            }

            world.everySeconds(5, offset = 2) {
                entity.upkeepRemoveExpiredBounties()
            }
        }

    }


    override fun createMenu(syncId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return BoardScreenHandler(syncId, playerInventory, getMaskedInventory(player), DoneProperty)
    }



    override fun getDisplayName(): Component {
        return Component.translatable(blockState.block.descriptionId)
    }

}
