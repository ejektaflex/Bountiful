package io.ejekta.kambrik

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikRegistrar
import io.ejekta.kambrik.registration.KambricAutoRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

class KambrikMod : ModInitializer {

    override fun onInitialize() {
        println("Hello world from Kambrik!")

        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            println("Got this: $it, ${it.entrypoint}, could do Kambrik init here")
            println("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }

    }

    companion object {
        const val ID = "kambrik"
    }

}