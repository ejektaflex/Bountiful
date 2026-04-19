package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.editor.BountifulEditorPersistence
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class DeletePoolEntryEdit(private val poolId: String, private val entryKey: String) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        BountifulEditorPersistence.deletePoolEntry(poolId, entryKey)
    }
}
