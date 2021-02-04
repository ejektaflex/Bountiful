package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.data.pool.Decree
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.DecreeList
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.content.BountyCreator
import io.ejekta.bountiful.common.content.DecreeItem
import io.ejekta.bountiful.common.content.gui.BoardScreenHandler
import io.ejekta.bountiful.common.mixin.SimpleInventoryAccessor
import io.ejekta.bountiful.common.util.content
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories

import net.minecraft.nbt.CompoundTag

import net.minecraft.block.BlockState

import net.minecraft.text.TranslatableText

import net.minecraft.entity.player.PlayerEntity

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.ListTag
import net.minecraft.screen.NamedScreenHandlerFactory

import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Tickable
import net.minecraft.world.World
import java.util.*


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), Tickable, NamedScreenHandlerFactory {

    //override val content = DefaultedList.ofSize(900, ItemStack.EMPTY)

    val decrees = SimpleInventory(3)

    private val bountyMap = mutableMapOf<UUID, BountyInventory>()

    private fun bountiesToSyncWith(player: PlayerEntity): BountyInventory? {
        val online = world?.players?.map { it.uuid } ?: return null
        val existing = online.mapNotNull { bountyMap[it] }
        return existing.maxByOrNull { it.numBounties }?.cloned(player.inventory.main)
    }

    fun updateCompletedBounties(player: PlayerEntity) {
        getBounties(player).completed += 1
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
            decrees.content.map { DecreeList[it].ids }.flatten().toSet()
        )
    }

    private fun randomlyAddBounty() {
        val ourWorld = world ?: return

        val slotToAddTo = BountyInventory.bountySlots.random()
        //println("Going to add to slow: $slotToAddTo")
        val slotsToRemove = (0 until listOf(0, 0, 1, 2).random()).map {
            (BountyInventory.bountySlots - slotToAddTo).random()
        }

        val commonBounty = BountyCreator.createBounty(getBoardDecrees(), 0)

        for (player in ourWorld.players) {
            val inv = getBounties(player)
            inv.addBounty(slotToAddTo, commonBounty)
            slotsToRemove.forEach { i -> inv.removeStack(i) }
        }

        // copy over server version

        // Cull offline players
        bountyMap.keys.forEach { uuid ->
            if ( uuid !in ourWorld.players.map { it.uuid }) {
                bountyMap.remove(uuid)
            }
        }

    }

    override fun tick() {
        val ourWorld = world ?: return
        if (ourWorld.isClient) return
        if ((ourWorld.time + 13L) % 20L == 0L) {
            randomlyAddBounty()
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

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)

        val decreeList = tag?.getCompound("decree_inv") ?: return
        Inventories.fromTag(
            decreeList,
            (decrees as SimpleInventoryAccessor).stacks
        )

        val bountyList = tag.getList("bounty_inv", 10) ?: return
        bountyList.forEach { tagged ->
            val userTag = tagged as CompoundTag
            val uuid = userTag.getUuid("uuid")
            val entry = bountiesToLoadTo(uuid)
            Inventories.fromTag(userTag, (entry as SimpleInventoryAccessor).stacks)
            entry.completed = userTag.getInt("completed")
        }

        println("Loaded tag $tag")
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)

        val decreeList = CompoundTag()
        Inventories.toTag(decreeList, decrees.content)

        val bountyList = ListTag()
        bountyMap.forEach { (uuid, inv) ->
            val userTag = CompoundTag()
            userTag.putUuid("uuid", uuid)
            Inventories.toTag(userTag, (inv as SimpleInventoryAccessor).stacks)
            userTag.putInt("completed", inv.completed)
            bountyList.add(userTag)
        }
        tag?.put("decree_inv", decreeList)
        tag?.put("bounty_inv", bountyList)

        println("Saved tag $tag")
        return tag
    }

}