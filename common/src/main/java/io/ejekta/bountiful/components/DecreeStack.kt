package io.ejekta.bountiful.components

import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.world.item.ItemStack

class DecreeStack(val stack: ItemStack) {
    var ids: Set<String>
        get() = component.ids
        set(value) { stack[BountifulContent.DECREE_DATA] = component.copy(ids = value) }

    var rank: Int
        get() = component.rank
        set(value) { stack[BountifulContent.DECREE_DATA] = component.copy(rank = value) }

    private val component: DecreeData
        get() = stack[BountifulContent.DECREE_DATA] ?: DecreeData.EMPTY
}