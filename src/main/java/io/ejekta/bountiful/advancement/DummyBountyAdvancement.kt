package io.ejekta.bountiful.advancement

import io.ejekta.bountiful.Bountiful
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.server.function.CommandFunction

// TODO check at the end if this is even needed?
// We have our own criterion but not yet sure how to trigger them
object DummyBountyAdvancement : Advancement(
    Bountiful.id("dummy_advancement"),
    null,
    null,
    AdvancementRewards(0, arrayOf(), arrayOf(), CommandFunction.LazyContainer(
        Bountiful.id("dummy_advancement_lazy_container")
    )),
    mapOf(),
    arrayOf<Array<String>>()
) {


}