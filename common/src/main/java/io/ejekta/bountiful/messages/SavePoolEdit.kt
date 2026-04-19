package io.ejekta.bountiful.messages

import io.ejekta.bountiful.content.editor.BountifulEditorPersistence
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Serializable

@Serializable
class SavePoolEdit(private val poolId: String) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        BountifulEditorPersistence.saveEmptyPool(poolId)
    }
}
