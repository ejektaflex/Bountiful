package io.ejekta.bountiful.chaos

import kotlinx.serialization.Serializable

@Serializable
data class MatSet(
    var parent: MatNode?,
    private val nodes: MutableSet<MatNode> = mutableSetOf()
) {

    fun addNode(newNode: MatNode) {
        newNode.parent?.removeNode(newNode)
        newNode.parent = this
        nodes.add(newNode)
    }

    private fun removeNode(currNode: MatNode) {
        nodes.remove(currNode)
        currNode.parent = null
    }

}