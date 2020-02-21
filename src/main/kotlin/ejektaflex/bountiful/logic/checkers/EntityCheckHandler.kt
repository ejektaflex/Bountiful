package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.StackPartition
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

class EntityCheckHandler() : CheckHandler<BountyEntryEntity>() {

    override fun fulfill() {
        println("EntityHandler nothing to fulfill!")
    }

    override fun objectiveStatus(): Map<BountyEntry, BountyProgress> {

        var succ = mutableMapOf<BountyEntry, BountyProgress>()

        val entityObjs = data.objectives.content.filterIsInstance<BountyEntryEntity>()

        for (obj in entityObjs) {
            succ[obj] = when (obj.killedAmount) {
                0 -> BountyProgress.NEW
                obj.amount -> BountyProgress.DONE
                else -> BountyProgress.UNFINISHED
            }
        }

        return succ
    }



}