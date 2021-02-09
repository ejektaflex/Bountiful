package io.ejekta.kambrik.ext

import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry


fun <T> SimpleRegistry<T>.register(id: Identifier, obj: T) {
    Registry.register(this, id, obj)
}

fun <T> SimpleRegistry<T>.registerForMod(modId: String, items: () -> Map<String, T>) {
    for ((itemId, item) in items()) {
        register(Identifier(modId, itemId), item)
    }
}
