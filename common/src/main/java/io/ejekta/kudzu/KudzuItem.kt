package io.ejekta.kudzu

interface KudzuItem {

    fun clone(): KudzuItem

    fun isVine() = this is KudzuVine
    fun isLeaf() = this is KudzuLeaf<*>
    fun asVine() = this as KudzuVine
    fun asLeaf() = this as KudzuLeaf<*>
    fun asLattice() = this as KudzuLattice
    fun asVineOrNull() = this as? KudzuVine
    fun asLeafOrNull() = this as? KudzuLeaf<*>
    fun asLatticeOrNull() = this as? KudzuLattice
}