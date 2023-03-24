package io.ejekta.bountiful.bridge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.content.messages.*
import io.ejekta.kambrik.Kambrik

interface BountifulSharedApi {

    fun isModLoaded(id: String): Boolean

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