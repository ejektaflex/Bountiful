package ejektaflex.bountiful.ext

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

operator fun IItemHandler.get(slotNum: Int): ItemStack {
    return getStackInSlot(slotNum)
}

operator fun IItemHandlerModifiable.set(slotNum: Int, stack: ItemStack) {
    return setStackInSlot(slotNum, stack)
}

val IItemHandlerModifiable.stacks: List<ItemStack>
    get() = slotRange.map { index ->
        this[index]
    }

val IItemHandlerModifiable.slotRange: IntRange
    get() = 0 until slots

val IItemHandlerModifiable.filledSlots: List<Int>
    get() = slotRange.mapNotNull {
        val itis = this[it]
        if (itis == ItemStack.EMPTY || itis.item == Items.AIR) {
            null
        } else {
            it
        }
    }



