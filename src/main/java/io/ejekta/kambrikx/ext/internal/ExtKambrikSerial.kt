package io.ejekta.kambrikx.ext.internal

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
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

fun <T> Decoder.doStructure(serial: SerialDescriptor, func: CompositeDecoder.() -> T): T {
    beginStructure(serial).apply {
        val result = func(this)
        endStructure(serial)
        return result
    }
}