package io.ejekta.bountiful.bridge

import io.ejekta.kambrik.bridge.LoaderBridge

class Bountybridge {
    companion object : BountifulSharedApi by LoaderBridge<BountifulSharedApi>(
        "io.ejekta.bountiful.BountifulFabricApi",
        "io.ejekta.bountiful.BountifulForgeApi"
    )()
}
