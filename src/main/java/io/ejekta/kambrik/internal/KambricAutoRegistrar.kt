package io.ejekta.kambrik.internal

import io.ejekta.kambrik.Kambrik
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry


interface KambricAutoRegistrar : Kambrik.KambrikMarker {
    val modId: String

    infix fun String.forItem(item: Item) = KambrikRegistrar.register(modId, Registry.ITEM, this, item)
    infix fun String.forBlock(item: Block) = KambrikRegistrar.register(modId, Registry.BLOCK, this, item)

}