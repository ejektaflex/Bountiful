package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryEntity
import ejektaflex.bountiful.logic.BountyProgress

class EntityCheckHandler() : CheckHandler<BountyEntryEntity>() {

    override fun fulfill() {
        // Nothing needs to happen in order to fulfill this bounty objective type :)
    }

    override fun objectiveStatus(): Map<BountyEntry, BountyProgress> {

        var succ = mutableMapOf<BountyEntry, BountyProgress>()

        val entityObjs = data.objectives.content.filterIsInstance<BountyEntryEntity>()

        for (obj in entityObjs) {
            succ[obj] = BountyProgress(obj.killedAmount to obj.amount)
        }

        return succ
    }



}