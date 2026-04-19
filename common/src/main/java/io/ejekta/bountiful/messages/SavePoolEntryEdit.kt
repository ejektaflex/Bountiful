package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.editor.BountifulEditorPersistence
import io.ejekta.bountiful.content.editor.PoolEntryEditorPayload
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class SavePoolEntryEdit(private val payload: PoolEntryEditorPayload) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        BountifulEditorPersistence.savePoolEntry(ctx.player, payload)
    }
}
