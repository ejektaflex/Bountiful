package io.ejekta.bountiful.bounty.types

import com.mojang.serialization.Lifecycle
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeNull
import io.ejekta.kambrik.ext.register
import net.minecraft.util.Identifier
import net.minecraft.util.registry.SimpleRegistry

object BountyTypeRegistry : SimpleRegistry<IBountyType>(Bountiful.BOUNTY_LOGIC_REGISTRY_KEY, Lifecycle.stable(), null) {

    val ITEM = register(Identifier("item"), BountyTypeItem())
    val NULL_KEY = Identifier("null")
    val NULL = register(NULL_KEY, BountyTypeNull())

}
