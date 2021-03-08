package io.ejekta.kambrik

import io.ejekta.kambrik.api.command.KambrikCommandApi
import io.ejekta.kambrik.api.file.KambrikFileApi
import io.ejekta.kambrik.api.logging.KambrikLoggingApi
import io.ejekta.kambrik.api.logging.KambrikLoggingMarkers
import io.ejekta.kambrik.api.structure.KambrikStructureApi
import org.apache.logging.log4j.LogManager

object Kambrik {

    val Command = KambrikCommandApi()

    val Structure = KambrikStructureApi()

    val File = KambrikFileApi()

    val Logging = KambrikLoggingApi()

    internal val Markers = KambrikLoggingMarkers()

    internal val Logger = LogManager.getLogger("Kambrik")

}
