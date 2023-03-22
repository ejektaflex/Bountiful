package io.ejekta.kambrik

import io.ejekta.bountiful.bridge.BountifulSharedApi
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

class BountifulForgeApi : BountifulSharedApi {

    init {
        ScreenHandlerType.register()
    }

}