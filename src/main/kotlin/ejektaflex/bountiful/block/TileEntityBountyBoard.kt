package ejektaflex.bountiful.block


import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent
import ejektaflex.bountiful.api.ext.*
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.api.ext.clear
import ejektaflex.bountiful.api.ext.filledSlots
import ejektaflex.bountiful.api.ext.slotRange
import ejektaflex.bountiful.logic.BountyData
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler



class TileEntityBountyBoard : TileEntity(), ITileEntityBountyBoard {

    override val inventory = ItemStackHandler(numSlots)
    override var newBoard = true

    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        tag.clear()
        tag.setTag("inv", inventory.serializeNBT())
        tag.setBoolean("newBoard", newBoard)
        return super.writeToNBT(tag)
    }

    override fun readFromNBT(tag: NBTTagCompound) {
        inventory.deserializeNBT(tag.getCompoundTag("inv"))
        newBoard = tag.getBoolean("newBoard")
        super.readFromNBT(tag)
    }

    override fun update() {
        if (!world.isRemote) {
            // Skip placement update tick
            if (newBoard) {
                newBoard = false
                return
            }

            if (world.totalWorldTime % 20 == 3L) {
                tickBounties()
            }
            if (world.totalWorldTime % Bountiful.config.boardAddFrequency == 3L) {
                addBounties()
            }

        }
    }

    private fun addBounties() {
        // Prune items to max amount - new amount
        while (inventory.filledSlots.size >= Bountiful.config.maxBountiesPerBoard && inventory.filledSlots.isNotEmpty()) {
            val slotPicked = inventory.filledSlots.random()
            inventory[slotPicked] = ItemStack.EMPTY
        }

        addSingleBounty()
        markDirty()
    }

    override fun addSingleBounty() {
        val newStack = BountyCreator.createStack(world)
        val fired = PopulateBountyBoardEvent.fireEvent(newStack, this)
        if (!fired.isCanceled) {
            inventory[inventory.slotRange.random()] = newStack
        }
    }

    private fun tickBounties() {
        val toRemove = mutableListOf<Int>()
        for (slot in inventory.slotRange) {
            val bounty = inventory.getStackInSlot(slot)
            if (bounty.item is ItemBounty) {
                val data = BountyData.from(bounty)
                val bountyItem = bounty.item as ItemBounty

                // Remove bountyStamp so that the timer is reset
                //bountyItem.removeTimestamp(bounty)

                if (Bountiful.config.shouldCountdownOnBoard) {
                    bountyItem.ensureTimerStarted(bounty, world)
                }

                if (data.hasExpired(world) || data.boardTimeLeft(world) <= 0) {
                    toRemove.add(slot)
                }
            }
        }
        toRemove.forEach { inventory.setStackInSlot(it, ItemStack.EMPTY) }
        markDirty()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) inventory as T else super.getCapability<T>(capability, facing)
    }

    companion object {
        const val numSlots = 27
    }

}
