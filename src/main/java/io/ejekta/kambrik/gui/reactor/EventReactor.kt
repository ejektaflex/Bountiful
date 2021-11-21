package io.ejekta.kambrik.gui.reactor

sealed class EventReactor(defaultCanPass: Boolean) {
    var canPassThrough: () -> Boolean = { defaultCanPass }
}