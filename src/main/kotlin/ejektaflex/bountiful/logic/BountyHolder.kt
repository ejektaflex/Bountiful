package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent
import ejektaflex.bountiful.api.ext.filledSlots
import ejektaflex.bountiful.api.ext.set
import ejektaflex.bountiful.api.ext.slotRange
import ejektaflex.bountiful.api.logic.IBountyHolder
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.ItemStackHandler


open class BountyHolder(override val handler: ItemStackHandler) : IBountyHolder, INBTSerializable<NBTTagCompound> by handler {
    override fun tickBounties(world: World) {
        val toRemove = mutableListOf<Int>()
        for (slot in handler.slotRange) {
            val bounty = handler.getStackInSlot(slot)
            if (bounty.item is ItemBounty) {
                // Try get bounty data. If it fails, just skip to the next bounty.
                val data = if (BountyData.isValidBounty(bounty)) {
                    BountyData.from(bounty)
                } else {
                    continue
                }

                val bountyItem = bounty.item as ItemBounty

                if (Bountiful.config.shouldCountdownOnBoard) {
                    bountyItem.ensureTimerStarted(bounty, world)
                }

                if (data.hasExpired(world) || data.boardTimeLeft(world) <= 0) {
                    toRemove.add(slot)
                }
            }
        }
        toRemove.forEach { handler.setStackInSlot(it, ItemStack.EMPTY) }
        //markDirty()
    }

    override fun addSingleBounty(world: World, te: ITileEntityBountyBoard?) {
        val newStack = BountyCreator.createStack(world)
        val fired = PopulateBountyBoardEvent.fireEvent(newStack, te)
        if (!fired.isCanceled) {
            val freeSlots = handler.slotRange.toList() - handler.filledSlots
            if (freeSlots.isNotEmpty()) {
                handler[freeSlots.random()] = newStack
                te?.sendRedstonePulse()
            }
        }
    }


    override fun update(world: World, te: ITileEntityBountyBoard?): Boolean {
        if (world.totalWorldTime % 20 == 3L) {
            tickBounties(world)
        }
        if (world.totalWorldTime % Bountiful.config.boardAddFrequency == 3L) {
            // Prune items to max amount - new amount
            while (handler.filledSlots.size >= Bountiful.config.maxBountiesPerBoard && handler.filledSlots.isNotEmpty()) {
                val slotPicked = handler.filledSlots.random()
                handler[slotPicked] = ItemStack.EMPTY
            }

            addSingleBounty(world, te)
            return true
        }
        return false
    }
}