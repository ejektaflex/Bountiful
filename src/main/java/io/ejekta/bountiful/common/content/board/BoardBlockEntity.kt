package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountifulContent
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
import java.util.*


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), Tickable, NamedScreenHandlerFactory {

    //override val content = DefaultedList.ofSize(900, ItemStack.EMPTY)

    private val decrees = SimpleInventory(3)

    private val bountyMap = mutableMapOf<UUID, BountyInventory>()

    private fun getBounties(uuid: UUID): BountyInventory {
        return bountyMap.getOrPut(uuid) { BountyInventory() }
    }

    private fun getBounties(player: PlayerEntity): BountyInventory {
        return getBounties(player.uuid)
    }

    private fun getEntireInventory(player: PlayerEntity): BoardInventory {
        return BoardInventory(getBounties(player), decrees)
    }

    private fun randomlyAddBounty() {
        val ourWorld = world ?: return

        val slotToAddTo = BountyInventory.bountySlots.random()
        //println("Going to add to slow: $slotToAddTo")
        val slotsToRemove = (0 until listOf(0, 0, 1, 2).random()).map {
            (BountyInventory.bountySlots - slotToAddTo).random()
        }

        val commonBounty = BountyData.defaultRandom().apply {
            timeStarted = ourWorld.time
            timeToComplete = 3000
        }

        for (player in ourWorld.players) {
            if (player.uuid in bountyMap) {
                val inv = getBounties(player.uuid)
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
            val entry = getBounties(uuid)
            Inventories.fromTag(userTag, (entry as SimpleInventoryAccessor).stacks)
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
            bountyList.add(userTag)
        }
        tag?.put("decree_inv", decreeList)
        tag?.put("bounty_inv", bountyList)

        println("Saved tag $tag")
        return tag
    }

}