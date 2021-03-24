package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.logic.*

enum class BountyType(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: IEntryLogic
) {
    NULL(isObj = false, isReward = true, NullLogic), // whatevs
    ITEM(isObj = true, isReward = true, ItemLogic)
}