package io.ejekta.kambrik.registration

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikRegistrar
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry


interface KambricAutoRegistrar : KambrikMarker {

    fun manualRegister()

    fun <T> String.forRegistration(reg: SimpleRegistry<T>, obj: T): T {
        return KambrikRegistrar.register(this@KambricAutoRegistrar, reg, this, obj)
    }

    infix fun String.forItem(item: Item): Item = forRegistration(Registry.ITEM, item)
    infix fun String.forBlock(block: Block): Block = forRegistration(Registry.BLOCK, block)




}