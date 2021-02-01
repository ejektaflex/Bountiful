package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.content.gui.BoardScreenHandler
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories

import net.minecraft.nbt.CompoundTag

import net.minecraft.block.BlockState

import net.minecraft.text.TranslatableText

import net.minecraft.entity.player.PlayerEntity

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.ListTag
import net.minecraft.screen.NamedScreenHandlerFactory

import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Tickable
import java.util.*


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), Tickable, NamedScreenHandlerFactory {

    //override val content = DefaultedList.ofSize(900, ItemStack.EMPTY)

    private val inventories = mutableMapOf<UUID, BoardInventory>()

    private fun getInventory(uuid: UUID): BoardInventory {
        return inventories.getOrPut(uuid) { BoardInventory() }
    }

    private fun getInventory(player: PlayerEntity): BoardInventory {
        return getInventory(player.uuid)
    }

    private fun randomlyAddBounty() {
        val ourWorld = world ?: return

        val slotToAddTo = BoardInventory.bountySlots.random()
        //println("Going to add to slow: $slotToAddTo")
        val slotsToRemove = (0 until listOf(0, 0, 1, 2).random()).map {
            (BoardInventory.bountySlots - slotToAddTo).random()
        }

        val commonBounty = BountyData.defaultRandom().apply {
            timeStarted = ourWorld.time
            timeToComplete = 3000
        }

        for (player in ourWorld.players) {

            if (player.uuid in inventories) {
                val inv = getInventory(player.uuid)
                inv.addBounty(slotToAddTo, commonBounty)
                slotsToRemove.forEach { i -> inv.removeStack(i) }
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
        return BoardScreenHandler(syncId, playerInventory, getInventory(player!!))
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        val bigList = tag?.getList("bbe", 10) ?: return
        bigList.forEach { tagged ->
            val userTag = tagged as CompoundTag
            val uuid = userTag.getUuid("uuid")
            val entry = getInventory(uuid)
            Inventories.fromTag(userTag, entry.content)
        }
    }

    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)
        val bigList = ListTag()
        inventories.forEach { entry ->
            val userTag = CompoundTag()
            userTag.putUuid("uuid", entry.key)
            Inventories.toTag(userTag, entry.value.content)
            bigList.add(userTag)
        }
        tag?.put("bbe", bigList)
        return tag
    }

}