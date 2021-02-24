package io.ejekta.kambrik

import io.ejekta.kambrik.api.command.KambrikCommandApi
import io.ejekta.kambrik.api.file.KambrikFileApi
import io.ejekta.kambrik.api.structure.KambrikStructureApi
import java.util.logging.Logger

object Kambrik {

    val Command = KambrikCommandApi()

    val Structure = KambrikStructureApi()

    val File = KambrikFileApi()

    internal val Log = Logger.getLogger("Kambrik")

}
