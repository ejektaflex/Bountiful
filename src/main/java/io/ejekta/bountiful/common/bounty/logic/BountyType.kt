package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.bounty.logic.entry.ItemLogic
import io.ejekta.bountiful.common.bounty.logic.entry.IEntryLogic

enum class BountyType(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: IEntryLogic
) {
    NULL(isObj = false, isReward = true, ItemLogic), // whatevs
    ITEM(isObj = true, isReward = true, ItemLogic)
}