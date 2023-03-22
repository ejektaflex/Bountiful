package io.ejekta.kudzu

sealed class KudzuLeaf<T>(val content: T) : KudzuItem {

    override fun toString() = content.toString()

    override fun clone(): KudzuLeaf<*> {
        return when(this) {
            is LeafDouble -> LeafDouble(content)
            is LeafInt -> LeafInt(content)
            is LeafString -> LeafString(content)
            is LeafBool -> LeafBool(content)
            is LeafNull -> LeafNull
        }
    }

    class LeafInt(num: Int) : KudzuLeaf<Int>(num)

    class LeafString(str: String) : KudzuLeaf<String>(str)

    class LeafDouble(double: Double) : KudzuLeaf<Double>(double)

    class LeafBool(bool: Boolean) : KudzuLeaf<Boolean>(bool)

    object LeafNull : KudzuLeaf<Nothing?>(null)

}