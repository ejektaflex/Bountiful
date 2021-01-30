package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.bounty.logic.entry.EntryItemLogic
import io.ejekta.bountiful.common.bounty.logic.entry.IEntryLogic
import net.minecraft.text.Text
import net.minecraft.text.LiteralText

enum class BountyType(
    val isObj: Boolean = true,
    val isReward: Boolean = false,
    val logic: IEntryLogic
) {
    NULL(isObj = false, isReward = true, EntryItemLogic), // whatevs
    ITEM(isObj = true, isReward = true, EntryItemLogic)
}