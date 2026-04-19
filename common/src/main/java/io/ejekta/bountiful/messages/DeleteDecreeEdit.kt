package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.editor.BountifulEditorPersistence
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class DeleteDecreeEdit(private val decreeId: String) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        BountifulEditorPersistence.deleteDecree(decreeId)
    }
}
