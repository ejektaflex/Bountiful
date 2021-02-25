package io.ejekta.kambrik.api.logging

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.LoggerContext

class KambrikLoggingApi internal constructor() {

    init {
        val ctx = LogManager.getContext(false) as LoggerContext
        ctx.reconfigure()
    }

    fun createLogger(modid: String): Logger {
        return LogManager.getLogger(modid)
    }

    fun createMarker(name: String, vararg parent: Marker?): Marker {
        val marker = MarkerManager.getMarker(name)
        marker.setParents(*parent)
        return marker
    }

}