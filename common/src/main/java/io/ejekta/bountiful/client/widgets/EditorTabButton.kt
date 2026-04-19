package io.ejekta.bountiful.client.widgets

import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import net.minecraft.network.chat.Component

class EditorTabButton(
    override val width: Int,
    val label: Component,
    val isActive: () -> Boolean,
    onSelect: () -> Unit
) : KWidget {

    override val height: Int = 16

    private val reactor = MouseReactor().apply {
        onClickDown = { _, _, _ -> onSelect() }
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area.reactWith(reactor)
        area.dsl {
            val active = isActive()
            area(width, height) {
                if (active) rect(0x41261b, 0xD0) else rect(0x21130b, 0xB0)
                onHover { if (!active) rect(0xFFFFFF, 0x22) }
            }
            val textColor = if (active) 0xFFFFE58A.toInt() else 0xFFD9C0A3.toInt()
            textCenteredColored(width / 2, (height - 8) / 2, label, textColor)
        }
    }
}
