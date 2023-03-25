package io.ejekta.bountiful.bridge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.messages.*
import io.ejekta.kambrik.Kambrik
import net.minecraft.client.item.ModelPredicateProviderRegistry

interface BountifulSharedApi {

    fun isModLoaded(id: String): Boolean

    fun registerItemDynamicTextures() {
        ModelPredicateProviderRegistry.register(
            BountifulContent.BOUNTY_ITEM,
            Bountiful.id("rarity")
        ) { stack, clientWorld, livingEntity, seed ->
            BountyInfo[stack].rarity.ordinal.toFloat() / 10f
        }

        ModelPredicateProviderRegistry.register(
            BountifulContent.DECREE_ITEM,
            Bountiful.id("status")
        ) { stack, clientWorld, livingEntity, seed ->
            val data = DecreeData[stack]
            if (data.ids.isNotEmpty()) 1f else 0f
        }
    }

    fun registerMessages() {
        Kambrik.Message.registerServerMessage(
            SelectBounty.serializer(),
            SelectBounty::class,
            Bountiful.id("select_bounty")
        )

        Kambrik.Message.registerClientMessage(
            ClipboardCopy.serializer(),
            ClipboardCopy::class,
            Bountiful.id("clipboard_copy")
        )

        Kambrik.Message.registerClientMessage(
            OnBountyComplete.serializer(),
            OnBountyComplete::class,
            Bountiful.id("play_sound_on_client")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyCriteriaObjective.serializer(),
            UpdateBountyCriteriaObjective::class,
            Bountiful.id("update_bounty_criteria")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyTooltipNotification.serializer(),
            UpdateBountyTooltipNotification::class,
            Bountiful.id("update_bounty_tooltip")
        )
    }

    fun registerScreenHandler(key: String) {

    }

}