package io.ejekta.bountiful.forge.test

import io.ejekta.bountiful.Bountiful
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterGameTestsEvent

@EventBusSubscriber(modid = Bountiful.ID, bus = EventBusSubscriber.Bus.MOD)
object BountifulNeoForgeGameTestBootstrap {

    @SubscribeEvent
    fun registerGameTests(evt: RegisterGameTestsEvent) {
        evt.register(BountifulNeoForgeGameTests::class.java)
    }
}
