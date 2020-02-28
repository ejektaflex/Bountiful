package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
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