package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.editor.BountifulEditorPersistence
import io.ejekta.bountiful.content.editor.DecreeEditorPayload
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class SaveDecreeEdit(private val payload: DecreeEditorPayload) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        BountifulEditorPersistence.saveDecree(payload)
    }
}
