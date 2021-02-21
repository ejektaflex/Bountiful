package io.ejekta.kambrik

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

internal class KambrikMod : ModInitializer {

    override fun onInitialize() {
        println("Hello world from Kambrik!")

        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            println("Got this: $it, ${it.entrypoint}, could do Kambrik init here")
            println("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }

    }

    internal companion object {
        const val ID = "kambrik"
    }

}