package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.ext.registryName
import ejektaflex.bountiful.api.ext.sendTranslation
import ejektaflex.bountiful.api.logic.picked.PickedEntryEntity
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
import ejektaflex.bountiful.data.BountyData
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min

object BountyChecker {

    /**
     * If requireExactNbt is true, simply checks whether the two stacks are the same item and have the same NBT data.
     * Otherwise checks whether the two stacks are the same item and that the stack includes all bounty NBT tags.
     */
    private fun validStackCheck(stack: ItemStack, bountystack: ItemStack): Boolean {
        if (Bountiful.config.requireExactNbt)
            return stack.isItemEqualIgnoreDurability(bountystack) && ItemStack.areItemStackTagsEqual(stack, bountystack)
        else
            return stack.isItemEqualIgnoreDurability(bountystack) && compareStackTags(stack, bountystack)
    }

    private fun compareStackTags(stack: ItemStack, bountystack: ItemStack): Boolean {
        //Require stack to have all tags present in bountystack
        val bountyCompound = bountystack.getTagCompound()
        if (bountyCompound == null || bountyCompound.isEmpty()) {
            //No tags required
            return true
        }

        return checkNestedCompound(bountyCompound, stack.getTagCompound())
    }
    
    private fun checkNestedCompound(bountyCompound: NBTTagCompound, stackCompound: NBTTagCompound?): Boolean {
        //Check if the stack has the required compound
        if (stackCompound == null)
            return false;

        //Compare all tags from the bounty compound
        for (key in bountyCompound.getKeySet()) {
            val a = bountyCompound.getTag(key)
            val b = stackCompound.getTag(key)

            //continue on match, return false otherwise

            if (a != null) {
                //nbtCompound tag exists and is not null

                if (a is NBTTagCompound && b is NBTTagCompound) {
                    //New tag compound that needs checking for a mismatch
                    if(checkNestedCompound(a, b)) {
                        continue
                    }
                    else {
                        return false
                    }
                }
                else if(a.equals(b)) {
                    continue;
                }
                else {
                    return false;
                }
            }
            else {
                //nbtCompound tag exists but is null (somehow)

                if(b == null) {
                    //Matching null
                    continue
                }
                else {
                    //stackCompound's tag of the same thing is not null
                    return false
                }
            }
        }

        //If this was reached, it's a match
        return true;
    }

    fun hasItems(player: EntityPlayer, inv: NonNullList<ItemStack>, data: BountyData): List<ItemStack>? {
        val stackPicked = data.toGet.items.mapNotNull { it as? PickedEntryStack }

        println(stackPicked)

        val prereqItems = inv.filter { invItem ->
            stackPicked.any { picked ->
                picked.itemStack?.isItemEqualIgnoreDurability(invItem) == true
            }
        }

        println("Prereq items: $prereqItems")

        // Check to see if bounty meets all prerequisites
        val hasAllItems = stackPicked.all { picked ->
            val stacksMatching = prereqItems.filter { validStackCheck(it, picked.itemStack!!) }
            val hasEnough = stacksMatching.sumBy { it.count } >= picked.amount
            if (!hasEnough) {
                player.sendTranslation("bountiful.cannot.fulfill")
            }
            hasEnough
        }

        return if (hasAllItems) {
            prereqItems
        } else {
            null
        }
    }

    fun takeItems(player: EntityPlayer, inv: NonNullList<ItemStack>, data: BountyData, matched: List<ItemStack>) {
        // If it does, reduce count of all relevant stacks
        data.toGet.items.mapNotNull { it as? PickedEntryStack }.forEach { picked ->
            val stacksToChange = matched.filter { validStackCheck(it, picked.itemStack!!) }
            for (stack in stacksToChange) {
                if (picked.amount == 0) {
                    break
                }
                val amountToRemove = min(stack.count, picked.amount)
                stack.count -= amountToRemove
                picked.amount -= amountToRemove
            }
        }
    }

    /**
     * Tries to tick all relevant entities on Bounty. Returns true if there are none left
     */
    fun tryTakeEntities(player: EntityPlayer, data: BountyData, bounty: ItemStack, entity: EntityLivingBase) {
        // Don't try take entities from an expired bounty.
        if (data.hasExpired(player.world)) {
            return
        }

        val bountyEntities = data.toGet.items.mapNotNull { it as? PickedEntryEntity }

        bountyEntities.forEach { picked ->
            if (picked.entityEntry?.registryName?.toString() == entity.registryName?.toString()) {
                if (picked.killedAmount < picked.amount) {
                    picked.killedAmount++
                }
            }
        }
        bounty.tagCompound = data.serializeNBT()
    }

    fun hasEntitiesFulfilled(data: BountyData): Boolean {
        val bountyEntities = data.toGet.items.mapNotNull { it as? PickedEntryEntity }
        return if (bountyEntities.isEmpty()) {
            true
        } else {
            bountyEntities.all { it.killedAmount == it.amount }
        }
    }

    fun rewardItems(player: EntityPlayer, data: BountyData, bountyItem: ItemStack) {

        // Reward player with rewards
        data.rewards.items.forEach { reward ->
            var amountNeededToGive = reward.amount
            val stacksToGive = mutableListOf<ItemStack>()
            while (amountNeededToGive > 0) {
                val stackSize = min(amountNeededToGive, bountyItem.maxStackSize)
                val newStack = reward.itemStack!!.copy().apply { count = stackSize }

                stacksToGive.add(newStack)
                amountNeededToGive -= stackSize
            }
            stacksToGive.forEach { stack ->
                ItemHandlerHelper.giveItemToPlayer(player, stack)
            }
        }
    }


}