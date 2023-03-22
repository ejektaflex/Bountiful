package io.ejekta.bountiful.bridge

import io.ejekta.kambrik.bridge.LoaderBridge

val bountiful_loader_bridge = LoaderBridge<BountifulSharedApi>()

val Bountybridge: BountifulSharedApi
    get() = bountiful_loader_bridge()
