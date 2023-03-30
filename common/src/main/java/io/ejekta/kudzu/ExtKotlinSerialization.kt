package io.ejekta.kudzu

import kotlinx.serialization.json.*

// ### JsonObject -> Kudzu

fun JsonElement.toKudzu(): KudzuItem {
    return when (this) {
        is JsonNull -> KudzuLeaf.LeafNull
        is JsonObject -> toKudzu()
        is JsonArray -> toKudzu()
        is JsonPrimitive -> toKudzu()
        else -> throw Exception("Something else shows in ${this::class.simpleName} when exporting!")
    }
}

fun JsonObject.toKudzu(): KudzuVine {
    return KudzuVine(map { it.key to it.value.toKudzu() }.toMap().toMutableMap())
}

fun JsonArray.toKudzu(): KudzuLattice {
    return KudzuLattice(map { it.toKudzu() }.toMutableList())
}

fun JsonPrimitive.toKudzu(): KudzuLeaf<*> {
    return when {
        isString -> KudzuLeaf.LeafString(content)
        booleanOrNull != null -> KudzuLeaf.LeafBool(boolean)
        intOrNull != null -> KudzuLeaf.LeafInt(int)
        doubleOrNull != null -> KudzuLeaf.LeafDouble(double)
        else -> throw Exception("Can't parse JsonPrimitive type to Kudzu! It's raw content is: '$content'")
    }
}

// ### Kudzu -> JsonObject

fun KudzuItem.toJsonElement(): JsonElement {
    return when (this) {
        is KudzuLeaf<*> -> toJsonElement()
        is KudzuVine -> toJsonObject()
        is KudzuLattice -> toJsonArray()
        else -> throw Exception("Kudzu Item not exportable!")
    }
}

fun KudzuVine.toJsonObject(): JsonObject {
    return JsonObject(content.map {
        it.key to it.value.toJsonElement()
    }.toMap())
}

fun KudzuLattice.toJsonArray(): JsonArray {
    return JsonArray(map { it.toJsonElement() })
}

fun KudzuLeaf<*>.toJsonElement(): JsonElement {
    return when (this) {
        is KudzuLeaf.LeafInt -> JsonPrimitive(content)
        is KudzuLeaf.LeafNull -> JsonNull
        is KudzuLeaf.LeafString -> JsonPrimitive(content)
        is KudzuLeaf.LeafBool -> JsonPrimitive(content)
        is KudzuLeaf.LeafDouble -> JsonPrimitive(content)
    }
}
