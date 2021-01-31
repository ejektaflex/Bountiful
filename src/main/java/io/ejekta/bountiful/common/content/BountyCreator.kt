package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.data.pool.Decree
import io.ejekta.bountiful.common.bounty.data.pool.Pool

object BountyCreator {

    fun getObjectivePoolsFor(decrees: Set<Decree>): Set<Pool> {
        return decrees.map { it.objectivePools }.flatten().toSet()
    }

    fun getRewardPoolsFor(decrees: Set<Decree>): Set<Pool> {
        return decrees.map { it.objectivePools }.flatten().toSet()
    }

    fun createBounty(decrees: Set<Decree>, rep: Int) {





    }


}