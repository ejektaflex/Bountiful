package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.DecreeItem
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.mixin.SimpleInventoryAccessor
import io.ejekta.bountiful.util.readOnlyCopy
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
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
    val bounties = BountyInventory()

    val bountyMasks = mutableMapOf<String, MutableSet<Int>>()

    fun isOnBoardFor(player: PlayerEntity, slot: Int): Boolean {
        return slot in bountyMasks.getOrDefault(player.uuidAsString, setOf())
    }

    private fun maskFor(player: PlayerEntity): MutableSet<Int> {
        return bountyMasks.getOrPut(player.uuidAsString) { mutableSetOf() }
    }

    fun putOnBoardFor(player: PlayerEntity, slot: Int) {
        maskFor(player).add(slot)
    }

    //private val bountyMap = mutableMapOf<UUID, BountyInventory>()

    private var finishMap = mutableMapOf<String, Int>()
    private val finishSerializer = MapSerializer(String.serializer(), Int.serializer())

    private fun getOnlinePlayerMasks(): List<MutableSet<Int>> {
        return world?.players?.mapNotNull { maskFor(it) } ?: listOf()
    }

    val invs = getOnlinePlayerMasks()

    private val level: Int
        get() {
            val total = finishMap.values.sum()
            return totalLevel(total)
        }

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

    private fun xpNeeded(done: Int, start: Int = 0): Int {
        val units = start + 1
        val unitTotal = units * 5
        return if (done >= unitTotal) {
            xpNeeded(done - unitTotal, units)
        } else {
            units
        }
    }

    private fun totalLevel(done: Int, per: Int = 2): Int {
        val unit = per * 5
        return if (done > unit) {
            5 + totalLevel(done - unit, per + 1)
        } else {
            done / per
        }
    }

    fun updateCompletedBounties(player: PlayerEntity) {
        val old = finishMap.getOrPut(player.uuidAsString) { 0 }
        finishMap[player.uuidAsString] = old + 1
    }

    private fun getBounties(player: PlayerEntity): BountyInventory {
        return bounties.cloned(maskFor(player))
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

        val commonBounty = BountyCreator.create(getBoardDecrees(), level, ourWorld.time)

        // Add to board
        bounties.addBounty(slotToAddTo, commonBounty)
        // Remove from board
        slotsToRemove.forEach { i -> bounties.removeStack(i) }

    }

    override fun tick() {
        val ourWorld = world ?: return
        if (ourWorld.isClient) return

        if ((ourWorld.time + 13L) % (20L * BountifulIO.configData.boardUpdateFrequency) == 0L) {
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

        val doneMap = tag.getCompound("completed")
        finishMap = NbtFormat.Default.decodeFromTag(finishSerializer, doneMap).toMutableMap()

        // TODO implement player mask loading
        /*
        val bountyList = tag.getList("bounty_inv", 10) ?: return
        bountyList.forEach { tagged ->
            val userTag = tagged as CompoundTag
            val uuid = userTag.getUuid("uuid")
            val entry = bountiesToLoadTo(uuid)
            Inventories.fromTag(userTag, (entry as SimpleInventoryAccessor).stacks)
        }

         */
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)

        val doneMap = NbtFormat.Default.encodeToTag(finishSerializer, finishMap)

        tag?.put("completed", doneMap)

        val decreeList = CompoundTag()
        Inventories.toTag(decreeList, decrees.readOnlyCopy)

        val bountyList = CompoundTag()
        Inventories.toTag(bountyList, bounties.readOnlyCopy)

        // TODO implement player mask saving
        /*
        val bountyList = ListTag()
        bountyMap.forEach { (uuid, inv) ->
            val userTag = CompoundTag()
            userTag.putUuid("uuid", uuid)
            Inventories.toTag(userTag, (inv as SimpleInventoryAccessor).stacks)
            //userTag.putInt("reputation", inv.level)
            bountyList.add(userTag)
        }

         */
        tag?.put("decree_inv", decreeList)
        tag?.put("bounty_inv", bountyList)

        //println("Saved tag $tag")
        return tag
    }

}