package io.ejekta.bountiful.common.content

import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.inventory.Inventories

import net.minecraft.nbt.CompoundTag

import net.minecraft.block.BlockState

import net.minecraft.text.TranslatableText

import net.minecraft.entity.player.PlayerEntity

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory

import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text


class BoardBlockEntity : BlockEntity(BountifulContent.BOARD_ENTITY), BoardInventory, NamedScreenHandlerFactory {

    override val content = DefaultedList.ofSize(9, ItemStack.EMPTY)

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return BoardScreenHandler(syncId, playerInventory, this)
    }



    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        Inventories.fromTag(tag, content)
    }

    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)
        Inventories.toTag(tag, content)
        return tag
    }

}