package ejektaflex.bountiful.ext

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

operator fun IItemHandler.get(slotNum: Int): ItemStack {
    return getStackInSlot(slotNum)
}

operator fun IItemHandlerModifiable.set(slotNum: Int, stack: ItemStack) {
    return setStackInSlot(slotNum, stack)
}

val IItemHandler.stacks: List<ItemStack>
    get() = slotRange.map { index ->
        this[index]
    }

val IItemHandler.slotRange: IntRange
    get() = 0 until slots

val IItemHandlerModifiable.filledBountySlots: List<Int>
    get() = slotRange.mapNotNull {
        val itis = this[it]
        if (itis == ItemStack.EMPTY || itis.item == Items.AIR) {
            null
        } else {
            it
        }
    }

fun IItemHandlerModifiable.filledSlots(inRange: IntRange): List<Int> {
    return inRange.mapNotNull {
        val itis = this[it]
        if (itis == ItemStack.EMPTY || itis.item == Items.AIR) {
            null
        } else {
            it
        }
    }
}



