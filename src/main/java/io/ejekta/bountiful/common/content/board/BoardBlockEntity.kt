package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.DecreeData
import io.ejekta.bountiful.common.config.BountifulIO
import io.ejekta.bountiful.common.config.Decree
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.content.BountyCreator
import io.ejekta.bountiful.common.content.DecreeItem
import io.ejekta.bountiful.common.content.gui.BoardScreenHandler
import io.ejekta.bountiful.common.mixin.SimpleInventoryAccessor
import io.ejekta.bountiful.common.config.Format
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import io.ejekta.bountiful.common.util.readOnlyCopy
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Tickable
import java.util.*


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), Tickable, ExtendedScreenHandlerFactory {

    //override val content = DefaultedList.ofSize(900, ItemStack.EMPTY)

    val decrees = SimpleInventory(3)

    private val bountyMap = mutableMapOf<UUID, BountyInventory>()

    private var finishMap = mutableMapOf<String, Int>()
    private val finishSerializer = MapSerializer(String.serializer(), Int.serializer())

    private fun getOnlinePlayerInventories(): List<BountyInventory> {
        val online = world?.players?.map { it.uuid } ?: return listOf()
        return online.mapNotNull { bountyMap[it] }
    }

    val invs = getOnlinePlayerInventories()

    val level: Int
        get() {
            val total = finishMap.values.sum()
            return totalLevel(total)
        }

    fun setDecree() {
        if (world is ServerWorld && decrees.isEmpty) {
            val slot = (0..2).random()
            val stack = DecreeItem.create()
            decrees.setStack(
                slot,
                stack
            )
        }
    }

    fun xpNeeded(done: Int, start: Int = 0): Int {
        val units = start + 1
        val unitTotal = units * 5
        return if (done >= unitTotal) {
            xpNeeded(done - unitTotal, units)
        } else {
            units
        }
    }

    fun totalLevel(done: Int, per: Int = 2): Int {
        val unit = per * 5
        return if (done > unit) {
            5 + totalLevel(done - unit, per + 1)
        } else {
            done / per
        }
    }

    private fun bountiesToSyncWith(player: PlayerEntity): BountyInventory? {
        return getOnlinePlayerInventories().maxByOrNull { it.numBounties }?.cloned(player.inventory.main)
    }

    fun updateCompletedBounties(player: PlayerEntity) {
        val old = finishMap.getOrPut(player.uuidAsString) { 0 }
        finishMap[player.uuidAsString] = old + 1
    }

    // only used for getting the profile to load bounties into
    private fun bountiesToLoadTo(uuid: UUID): BountyInventory {
        return bountyMap.getOrPut(uuid) {
            BountyInventory()
        }
    }

    private fun getBounties(player: PlayerEntity): BountyInventory {
        return bountyMap.getOrPut(player.uuid) {
            bountiesToSyncWith(player) ?: BountyInventory()
        }
    }

    private fun getEntireInventory(player: PlayerEntity): BoardInventory {
        return BoardInventory(getBounties(player), decrees)
    }

    private fun getBoardDecrees(): Set<Decree> {
        return BountifulContent.getDecrees(
            decrees.readOnlyCopy.map { DecreeData[it].ids }.flatten().toSet()
        )
    }

    private fun randomlyAddBounty() {
        val ourWorld = world ?: return

        if (decrees.isEmpty) {
            return
        }

        val slotToAddTo = BountyInventory.bountySlots.random()
        //println("Going to add to slow: $slotToAddTo")
        val slotsToRemove = (0 until listOf(0, 0, 0, 1, 1, 2, 2).random()).map {
            (BountyInventory.bountySlots - slotToAddTo).random()
        }

        val commonBounty = BountyCreator.createBounty(getBoardDecrees(), level, ourWorld.time)

        for (player in ourWorld.players.toMutableList()) {
            val inv = getBounties(player)
            inv.addBounty(slotToAddTo, commonBounty)
            slotsToRemove.forEach { i -> inv.removeStack(i) }
        }

        if (ourWorld.players.isNotEmpty()) {
            // Cull offline players
            bountyMap.keys.toMutableList().forEach { uuid ->
                if ( uuid !in ourWorld.players.map { it.uuid }) {
                    bountyMap.remove(uuid)
                }
            }
        }

    }

    override fun tick() {
        val ourWorld = world ?: return
        if (ourWorld.isClient) return

        val updateFreq = 45
        if ((ourWorld.time + 13L) % (20L * BountifulIO.config.boardUpdateFrequency) == 0L) {
            // Change bounty population
            randomlyAddBounty()

            // Set unset decrees
            (decrees as SimpleInventoryAccessor).stacks.forEach { stack ->
                DecreeData.edit(stack) {
                    if (ids.isEmpty() && BountifulContent.Decrees.isNotEmpty()) {
                        ids.add(BountifulContent.Decrees.random().id)
                    }
                }
            }

        }



    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return BoardScreenHandler(syncId, playerInventory, getEntireInventory(player!!))
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    override fun writeScreenOpeningData(serverPlayerEntity: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
        setDecree()
        packetByteBuf.writeInt(level)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)

        val decreeList = tag?.getCompound("decree_inv") ?: return
        Inventories.fromTag(
            decreeList,
            (decrees as SimpleInventoryAccessor).stacks
        )

        val doneMap = tag.getCompound("completed").toJson()
        finishMap = Format.Normal.decodeFromJsonElement(finishSerializer, doneMap).toMutableMap()

        val bountyList = tag.getList("bounty_inv", 10) ?: return
        bountyList.forEach { tagged ->
            val userTag = tagged as CompoundTag
            val uuid = userTag.getUuid("uuid")
            val entry = bountiesToLoadTo(uuid)
            Inventories.fromTag(userTag, (entry as SimpleInventoryAccessor).stacks)
        }

        println("Loaded tag $tag")
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)

        val doneMap = Format.Normal.encodeToJsonElement(finishSerializer, finishMap).toTag() as CompoundTag

        tag?.put("completed", doneMap)

        val decreeList = CompoundTag()
        Inventories.toTag(decreeList, decrees.readOnlyCopy)

        val bountyList = ListTag()
        bountyMap.forEach { (uuid, inv) ->
            val userTag = CompoundTag()
            userTag.putUuid("uuid", uuid)
            Inventories.toTag(userTag, (inv as SimpleInventoryAccessor).stacks)
            //userTag.putInt("reputation", inv.level)
            bountyList.add(userTag)
        }
        tag?.put("decree_inv", decreeList)
        tag?.put("bounty_inv", bountyList)

        println("Saved tag $tag")
        return tag
    }

}