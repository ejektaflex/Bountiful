package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.bounty.types.builtin.*

enum class BountyTypeOldEnum(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: (entry: BountyDataEntry) -> IBountyType
) {
    NULL(isObj = false, isReward = true, { BountyTypeNull }), // whatevs
    ITEM(isObj = true, isReward = true, { BountyTypeItem }),
    ENTITY(isObj = true, isReward = false, { BountyTypeEntity }),
    ITEM_TAG(isObj = true, isReward = false, { BountyTypeItemTag }),
    COMMAND(isObj = false, isReward = true, { BountyTypeCommand }),
    CRITERIA(isObj = true, isReward = false, { BountyTypeCriteria })
}