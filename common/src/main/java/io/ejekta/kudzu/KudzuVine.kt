package io.ejekta.kudzu

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@KudzuMarker
@Serializable(with = KudzuSerializer::class)
class KudzuVine(
    val content: MutableMap<String, KudzuItem> = mutableMapOf()
) : KudzuItem, MutableMap<String, KudzuItem> by content {

    @KudzuMarker
    operator fun invoke(func: KudzuVine.() -> Unit) = apply(func)



    override fun clone(): KudzuVine {
        return KudzuVine(content.map {
            it.key to it.value.clone()
        }.toMap().toMutableMap())
    }

    // Returns true if the vine is now empty
    fun prune(other: KudzuVine): Boolean {
        val commonKeys = keys.intersect(other.keys)

        // prune common keys
        for ((key, item) in this.filter { it.key in commonKeys }) {
            when (item) {
                is KudzuLeaf<*> -> {
                    val otherVal = other[key]!!.asLeaf()
                    if (item.content == otherVal.content) {
                        trim(key)
                    }
                }
                is KudzuVine -> {
                    val pruned = item.prune(other[key]!!.asVine())
                    if (pruned) {
                        trim(key)
                    }
                }
                is KudzuLattice -> {
                    val pruned = item.prune(other[key]!!.asLattice())
                    if (pruned) {
                        trim(key)
                    }
                }
            }
        }

        return isEmpty()
    }

    override fun toString(): String {
        return toJsonObject().toString()
    }

    fun leaf(key: String, value: Int?) {
        this[key] = if (value != null) {
            KudzuLeaf.LeafInt(value)
        } else {
            KudzuLeaf.LeafNull
        }
    }

    fun leaf(key: String, value: String?) {
        this[key] = if (value != null) {
            KudzuLeaf.LeafString(value)
        } else {
            KudzuLeaf.LeafNull
        }
    }

    fun leaf(key: String, value: Boolean?) {
        this[key] = if (value != null) {
            KudzuLeaf.LeafBool(value)
        } else {
            KudzuLeaf.LeafNull
        }
    }

    fun leaf(key: String, value: Double?) {
        this[key] = if (value != null) {
            KudzuLeaf.LeafDouble(value)
        } else {
            KudzuLeaf.LeafNull
        }
    }

    fun leaf(key: String, value: Nothing? = null) {
        this[key] = KudzuLeaf.LeafNull
    }

    fun leaf(key: String, value: KudzuItem) {
        this[key] = value
    }

    fun <T> leaf(key: String, serializer: KSerializer<T>, value: T) {
        this[key] = Json.encodeToJsonElement(serializer, value).toKudzu()
    }

    fun lattice(key: String, func: KudzuLattice.() -> Unit = {}) {
        this[key] = KudzuLattice().apply(func)
    }

    fun trim(vararg index: String) = trim(index.toList())

    fun trim(index: List<String>): KudzuItem {
        if (index.isEmpty()) {
            throw Exception("KudzuVine::trim received an empty list of arguments!")
        }
        val targetVine = stem(index.drop(1))
        return targetVine.remove(index[0]) ?: throw Exception("Key did not exist in Vine!: ${index[0]}")
    }

    fun vine(vararg index: String) = vine(index.toList())

    fun vine(vararg index: String, vineFunc: KudzuVine.() -> Unit): KudzuVine {
        return vine(*index).apply(vineFunc)
    }

    fun vine(index: List<String>): KudzuVine {
        val key = index.firstOrNull() ?: return this
        val oldVine = this[key] as? KudzuVine ?: KudzuVine()
        this[key] = oldVine
        return oldVine.vine(index.drop(1))
    }

    fun stem(vararg index: String) = stem(index.toList())

    fun stem(vararg index: String, queryFunc: KudzuVine.() -> Unit): KudzuVine {
        return stem(*index).apply(queryFunc)
    }

    fun stem(index: List<String>): KudzuVine {
        val key = index.firstOrNull() ?: return this
        val gotVine = this[key] as? KudzuVine ?: throw Exception("Key is not a vine!: $index")
        return gotVine.stem(index.drop(1))
    }

    fun safeStem(vararg index: String) = safeStem(index.toList())

    fun safeStem(vararg index: String, queryFunc: KudzuVine.() -> Unit): KudzuVine? {
        return safeStem(*index)?.apply(queryFunc)
    }

    fun safeStem(index: List<String>): KudzuVine? {
        val key = index.firstOrNull() ?: return this
        val gotVine = this[key] as? KudzuVine
        return gotVine?.safeStem(index.drop(1))
    }

    fun graft(other: KudzuVine) {
        for (entry in other) {
            when(val item = entry.value) {
                is KudzuLeaf<*> -> this[entry.key] = item.clone()
                is KudzuVine -> vine(entry.key).graft(item.clone())
                // is KudzuLattice
                is KudzuLattice -> lattice(entry.key) { // TODO lattice item grafting
                    for (i in item) {
                        add(i)
                    }
                }
                else -> throw Exception("Cannot graft ${entry.key} with value ${entry.value}")
            }
        }
    }

    fun growIsNull(vararg index: String): Boolean {
        val key = index.firstOrNull() ?: return false
        val root = stem(index.dropLast(1).toList())
        val item = root[index.last()]!!.asLeaf()
        return item is KudzuLeaf.LeafNull
    }

    private fun <T : Any?> growLeafOrNull(keys: List<String>): T? {
        val item = stem(keys.dropLast(1))[keys.last()]!!
        if (item is KudzuLeaf.LeafNull) return null
        val leaf = item as? KudzuLeaf<*>
            ?: throw Exception("$keys does not lead to a leaf!")
        return leaf.content as T?
    }

    private fun <T : Any> growLeaf(keys: List<String>): T =
        growLeafOrNull(keys) ?: throw NullPointerException("Leaf not found in vine! Path: $keys")

    fun growInt(vararg keys: String): Int = growInt(keys.toList())
    fun growInt(keys: List<String>): Int = growLeaf(keys)
    fun growIntOrNull(vararg keys: String): Int? = growIntOrNull(keys.toList())
    fun growIntOrNull(keys: List<String>): Int? = growLeafOrNull(keys)

    fun growString(vararg keys: String): String = growString(keys.toList())
    fun growString(keys: List<String>): String = growLeaf(keys)
    fun growStringOrNull(vararg keys: String): String? = growStringOrNull(keys.toList())
    fun growStringOrNull(keys: List<String>): String? = growLeafOrNull(keys)

    fun growBool(vararg keys: String): Boolean = growBool(keys.toList())
    fun growBool(keys: List<String>): Boolean = growLeaf(keys)
    fun growBoolOrNull(vararg keys: String): Boolean? = growBoolOrNull(keys.toList())
    fun growBoolOrNull(keys: List<String>): Boolean? = growLeafOrNull(keys)

    fun growDouble(vararg keys: String): Double = growDouble(keys.toList())
    fun growDouble(keys: List<String>): Double = growLeaf(keys)
    fun growDoubleOrNull(vararg keys: String): Double? = growDoubleOrNull(keys.toList())
    fun growDoubleOrNull(keys: List<String>): Double? = growLeafOrNull(keys)

    fun <T> growListOf(serializer: KSerializer<T>, key: String): List<T> {
        TODO("Implement growListOf")
    }

    fun <T> growMapOf(serializer: KSerializer<T>, key: String): Map<String, T> {
        TODO("Implement growMapOf")
    }

}