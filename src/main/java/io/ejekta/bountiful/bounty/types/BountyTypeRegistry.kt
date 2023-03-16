package io.ejekta.bountiful.bounty.types

import com.mojang.serialization.Lifecycle
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.builtin.*
import io.ejekta.kambrik.ext.register
import net.minecraft.registry.SimpleRegistry

object BountyTypeRegistry : SimpleRegistry<IBountyType>(Bountiful.BOUNTY_LOGIC_REGISTRY_KEY, Lifecycle.stable(), false) {
    val ITEM = BountyTypeItem().apply { register(id, this) }
    val ENTITY = BountyTypeEntity().apply { register(id, this) }
    val ITEM_TAG = BountyTypeItemTag().apply { register(id, this) }
    val COMMAND = BountyTypeCommand().apply { register(id, this) }
    val CRITERIA = BountyTypeCriteria().apply { register(id, this) }
}
