package io.ejekta.kambrik.ext

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry

val Item.id: Identifier
    get() = Registry.ITEM.getId(this)

val ItemStack.id: Identifier
    get() = item.id