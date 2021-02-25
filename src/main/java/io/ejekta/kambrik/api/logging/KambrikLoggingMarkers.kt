package io.ejekta.kambrik.api.logging

import io.ejekta.kambrik.Kambrik

class KambrikLoggingMarkers internal constructor() {

    val Rendering = Kambrik.Logging.createMarker("Rendering")

    val NBT = Kambrik.Logging.createMarker("NBT")

}