package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.Bountiful
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.util.registry.Registry

object BountifulContent {

    val BOUNTY_ITEM = BountyItem()

    fun register() {
        CommandRegistrationCallback.EVENT.register(BountifulCommands.registerCommands())
        Registry.register(Registry.ITEM, Bountiful.id("bounty"), BOUNTY_ITEM)
    }

}