package io.ejekta.kambrikx.ext.internal

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

fun Encoder.doCollection(serial: SerialDescriptor, size: Int, func: CompositeEncoder.() -> Unit) {
    beginCollection(serial, size).apply {
        func(this)
        endStructure(serial)
    }
}

fun Encoder.doStructure(serial: SerialDescriptor, func: CompositeEncoder.() -> Unit) {
    beginStructure(serial).apply {
        func(this)
        endStructure(serial)
    }
}