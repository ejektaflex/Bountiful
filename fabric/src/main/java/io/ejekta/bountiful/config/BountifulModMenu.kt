package io.ejekta.bountiful.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader

class BountifulModMenu : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return null
        }

        return ConfigScreenFactory {
            BountifulConfigScreen.buildScreen()
        }
    }
}
