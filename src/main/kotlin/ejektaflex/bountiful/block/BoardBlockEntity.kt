package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.ext.*
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.ItemStackHandler

class BoardBlockEntity(inPos: BlockPos, inState: BlockState) : BlockEntity(BountifulContent.BOUNTYTILEENTITY, inPos, inState) {


    // Lazy load lazy optional ( ... :| )
    private val lazyOptional: LazyOptional<*> by lazy {
        LazyOptional.of { handler }
    }

    val handler: ItemStackHandler by lazy {
        ItemStackHandler(SIZE)
    }

    private val bountyRange = 0 until 21
    private val decreeRange = 21 until 24

    private val bountySlots = bountyRange.toList()
    private val decreeSlots = decreeRange.toList()

    private val filledBountySlots: List<Int>
        get() = handler.filledSlots(bountyRange)

    private val hasDecree: Boolean
        get() = numDecrees > 0

    val numDecrees: Int
        get() = decreeSlots.map { handler.getStackInSlot(it) }.count { it.item is ItemDecree }

    private val decrees: List<Decree>
        get() {
            val ids = decreeSlots.map {
                handler.getStackInSlot(it)
            }.filter {
                it.item is ItemDecree
            }.map {
                it.edit<ItemDecree> { stack -> ensureDecree(stack) }
                it.tag!!.getUnsortedList("ids").map { decr ->
                    decr.getString("id")
                }
            }.flatten()

            return ids.mapNotNull { DecreeRegistry.getDecree(it) }
        }

    private fun tickBounties() {

        val toRemove = mutableListOf<Int>()

        for (slot in handler.slotRange) {
            val bounty = handler.getStackInSlot(slot)
            if (bounty.item is ItemBounty) {
                // Try get bounty data. If it fails, just skip to the next bounty.
                val data = if (BountyData.isValidBounty(bounty)) {
                    bounty.toData(::BountyData)
                } else {
                    continue
                }

                val bountyItem = bounty.item as ItemBounty

                if (BountifulConfig.SERVER.shouldCountdownOnBoard.get()) {
                    bountyItem.ensureTimerStarted(bounty, world!!)
                }

                if (data.hasExpired(world!!) || data.boardTimeLeft(world!!) <= 0) {
                    toRemove.add(slot)
                }

            }
        }

        toRemove.forEach { handler.setStackInSlot(it, ItemStack.EMPTY) }

        if (toRemove.isNotEmpty()) {
            markDirty()
        }

    }

    private fun addSingleBounty() {
        //println("Adding a single bounty")
        val newStack = ItemBounty.create(world!!, decrees)
        val freeSlots = bountySlots - filledBountySlots
        if (freeSlots.isNotEmpty()) {
            handler[freeSlots.hackyRandom()] = newStack
        }
    }


    private fun ensureDecreeExists() {
        if (!hasDecree) {
            handler.setStackInSlot(decreeSlots.hackyRandom(), ItemDecree.makeStack())
        }
        // Initialize NBT data for new decrees

        for (decSlot in decreeSlots) {
            val stack = handler.getStackInSlot(decSlot)
            if (stack.item is ItemDecree && !stack.hasTag()) {
                (stack.item as ItemDecree).ensureDecree(stack)
            }
        }

    }

    override fun tick() {
        if (!world!!.isRemote) {
            if (world!!.gameTime % 20 == 0L) {
                ensureDecreeExists()
                if (hasDecree) {
                    tickBounties()
                }
            }
            if (world!!.gameTime % (BountifulConfig.SERVER.boardAddFrequency.get() * 20L) == 3L) {

                // Prune items to max amount - new amount
                while (filledBountySlots.size >= BountifulConfig.SERVER.maxBountiesPerBoard.get() && filledBountySlots.isNotEmpty()) {
                    val slotPicked = filledBountySlots.hackyRandom()
                    handler[slotPicked] = ItemStack.EMPTY
                }

                if (hasDecree) {
                    addSingleBounty()
                }

                markDirty()

            }
        }
    }

    var newBoard = true
    var pulseLeft = 0


    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            put("inv", handler.serializeNBT())
            putBoolean("newBoard", newBoard)
            putInt("pulseLeft", pulseLeft)
        }
    }


    override fun deserializeNBT(nbt: CompoundTag) {
        handler.deserializeNBT(nbt.getCompound("inv"))
        newBoard = nbt.getBoolean("newBoard")
        pulseLeft = nbt.getInt("pulseLeft")
    }

    // Remove side when you want to remove hopper access
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyOptional as LazyOptional<T>
        }

        return super.getCapability(cap)
    }

    override fun createMenu(i: Int, inv: PlayerInventory, player: PlayerEntity): Container? {
        return BoardContainer(i, inv, this)
        //return BoardContainer(i, world!!, pos, inv)
    }


    companion object {
        const val SIZE = 24
    }

}