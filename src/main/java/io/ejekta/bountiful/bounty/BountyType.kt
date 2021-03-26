package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.logic.*

enum class BountyType(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: (entry: BountyDataEntry) -> IEntryLogic
) {
    NULL(isObj = false, isReward = true, { NullLogic }), // whatevs
    ITEM(isObj = true, isReward = true, ::ItemLogic),
    ENTITY(isObj = true, isReward = false, ::EntityLogic)
}