package io.ejekta.bountiful.common.bounty

import io.ejekta.bountiful.common.bounty.logic.IEntryLogic
import io.ejekta.bountiful.common.bounty.logic.ItemLogic

enum class BountyType(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: IEntryLogic
) {
    NULL(isObj = false, isReward = true, ItemLogic), // whatevs
    ITEM(isObj = true, isReward = true, ItemLogic)
}