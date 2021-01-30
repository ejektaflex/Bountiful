package io.ejekta.bountiful.common.content

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.inventory.Inventories

import net.minecraft.nbt.CompoundTag

import net.minecraft.block.BlockState

import net.minecraft.text.TranslatableText

import net.minecraft.entity.player.PlayerEntity

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.NamedScreenHandlerFactory

import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.*


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), NamedScreenHandlerFactory {

    //override val content = DefaultedList.ofSize(900, ItemStack.EMPTY)

    val inventories = mutableMapOf<UUID, UniqueInv>()

    private fun getInventory(uuid: UUID): UniqueInv {
        return inventories.getOrPut(uuid) { UniqueInv() }
    }

    private fun getInventory(player: PlayerEntity): UniqueInv {
        return getInventory(player.uuid)
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
        inventories.map { entry ->
            val userTag = CompoundTag()
            userTag.putUuid("uuid", entry.key)
            Inventories.toTag(userTag, entry.value.content)
            bigList.add(userTag)
        }
        tag?.put("bbe", bigList)
        return tag
    }

}