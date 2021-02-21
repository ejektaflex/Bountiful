package io.ejekta.kambrik

import io.ejekta.kambrik.api.command.KambrikCommandApi
import io.ejekta.kambrik.api.structure.KambrikStructureApi

object Kambrik {

    val Command = KambrikCommandApi()

    val Structure = KambrikStructureApi()

}
