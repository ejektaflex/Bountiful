package io.ejekta.kudzu

class KudzuLattice(
    val content: MutableList<KudzuItem> = mutableListOf()
) : KudzuItem, MutableList<KudzuItem> by content {

    fun slat(item: String?) = add(if (item != null) KudzuLeaf.LeafString(item) else KudzuLeaf.LeafNull)
    fun slat(item: Double?) = add(if (item != null) KudzuLeaf.LeafDouble(item) else KudzuLeaf.LeafNull)
    fun slat(item: Int?) = add(if (item != null) KudzuLeaf.LeafInt(item) else KudzuLeaf.LeafNull)
    fun slat(item: Boolean?) = add(if (item != null) KudzuLeaf.LeafBool(item) else KudzuLeaf.LeafNull)
    fun slat(item: Nothing? = null) = add(KudzuLeaf.LeafNull)

    fun slat(func: KudzuVine.() -> Unit = {}) {
        val item = KudzuVine()
        add(item.apply(func))
    }

    override fun clone(): KudzuLattice {
        return KudzuLattice(this.content.map {
            it.clone()
        }.toMutableList())
    }

    fun prune(other: KudzuLattice): Boolean {
        val commonIndices = (0 until size).intersect(0 until other.size)
        val matchedItems = commonIndices
            .map { it to this[it] }
            .filter { it.first in commonIndices }

        //retainAll()

        val toRemove = mutableListOf<Int>()

        for ((index, item) in matchedItems) {
            when (item) {
                is KudzuLeaf<*> -> {
                    toRemove.add(index)
                }
                is KudzuVine -> {
                    val otherVine = other[index].asVineOrNull() ?: continue
                    val pruned = item.prune(otherVine)
                    if (pruned) {
                        toRemove.add(index)
                    }
                }
                is KudzuLattice -> {
                    val otherLattice = other[index].asLatticeOrNull() ?: continue
                    val pruned = item.prune(otherLattice)
                    if (pruned) {
                        toRemove.add(index)
                    }
                }
            }
        }

        for (i in toRemove.size - 1 downTo 0) {
            removeAt(i)
        }

        return isEmpty()
    }

}