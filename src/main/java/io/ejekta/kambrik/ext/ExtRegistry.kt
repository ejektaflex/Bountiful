package io.ejekta.kambrik.ext

import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


fun <T> Registry<T>.register(id: Identifier, obj: T) {
    Registry.register(this, id, obj)
}

fun <T> Registry<T>.registerForMod(modId: String, items: () -> Map<String, T>) {
    for ((itemId, item) in items()) {
        register(Identifier(modId, itemId), item)
    }
}
