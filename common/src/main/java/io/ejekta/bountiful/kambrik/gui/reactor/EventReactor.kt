package io.ejekta.bountiful.kambrik.gui.reactor

sealed class EventReactor(defaultCanPass: Boolean) {
    /**
     * Whether widgets behind this widget can be clicked on.
     */
    var canPassThrough: () -> Boolean = { defaultCanPass }
}