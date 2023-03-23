package io.ejekta.bountiful.bridge

import io.ejekta.kambrik.bridge.LoaderBridge

val Bountybridge by lazy {
    LoaderBridge<BountifulSharedApi>(
        "io.ejekta.bountiful.BountifulFabricApi",
        "io.ejekta.bountiful.BountifulForgeApi"
    )()
}
