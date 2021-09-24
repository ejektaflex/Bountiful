package io.ejekta.kambrik.gui.toolkit

interface TKElementHolder {
    var isExpanded: Boolean
    val elements: MutableList<TKElement>
}