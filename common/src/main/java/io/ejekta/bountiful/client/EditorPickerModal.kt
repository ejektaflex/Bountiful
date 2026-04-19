package io.ejekta.bountiful.client

import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.gui.draw.widgets.KListWidget
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class EditorPickerModal(
    private val parent: EditorScreen,
    private val title: Component,
    allOptions: List<String>,
    initialSelected: Collection<String>,
    private val onDone: (List<String>) -> Unit
) {
    private val allSorted: List<String> = allOptions.distinct().sorted()
    private val selected: MutableSet<String> = initialSelected.toMutableSet()

    private val availableItems: () -> List<String> = { allSorted.filter { it !in selected } }
    private val activeItems: () -> List<String> = { allSorted.filter { it in selected } }

    private val availableScroll = KScrollbarVertical(LIST_H, 6, SCROLL_KNOB_H, SCROLLER, 0x0)
    private val activeScroll = KScrollbarVertical(LIST_H, 6, SCROLL_KNOB_H, SCROLLER, 0x0)

    private val availableList = KListWidget(
        items = availableItems,
        itemWidth = COL_W,
        itemHeight = ROW_H,
        shown = ROWS_SHOWN,
        orientation = KListWidget.Orientation.VERTICAL,
        mode = KListWidget.Mode.SINGLE,
        onDrawItemFunc = { _, item, _ -> drawRow(item, added = false) }
    ).apply {
        attachScrollbar(availableScroll)
        reactor.canPassThrough = { true }
        reactor.onClickDown = { _, ry, _ -> clickAvailable(ry) }
    }

    private val activeList = KListWidget(
        items = activeItems,
        itemWidth = COL_W,
        itemHeight = ROW_H,
        shown = ROWS_SHOWN,
        orientation = KListWidget.Orientation.VERTICAL,
        mode = KListWidget.Mode.SINGLE,
        onDrawItemFunc = { _, item, _ -> drawRow(item, added = true) }
    ).apply {
        attachScrollbar(activeScroll)
        reactor.canPassThrough = { true }
        reactor.onClickDown = { _, ry, _ -> clickActive(ry) }
    }

    private val backdropReactor = MouseReactor().apply {
        canPassThrough = { false }
        onClickDown = { _, _, _ -> /* swallow */ }
    }

    private val doneReactor = MouseReactor().apply {
        onClickDown = { _, _, _ -> finish() }
    }

    private var closed = false

    fun isOpen(): Boolean = !closed

    fun drawInto(dsl: KGuiDsl) {
        if (closed) return
        with(dsl) {
            // Full-screen backdrop blocks clicks on underlying UI
            area(EditorScreen.GUI_WIDTH, EditorScreen.GUI_HEIGHT) {
                reactWith(backdropReactor)
                rect(0x000000, 0xB0)
            }

            val panelX = (EditorScreen.GUI_WIDTH - PANEL_W) / 2
            val panelY = (EditorScreen.GUI_HEIGHT - PANEL_H) / 2
            offset(panelX, panelY) {
                area(PANEL_W, PANEL_H) { rect(0x2A1A10, 0xFF) }
                area(PANEL_W, PANEL_H) { rect(0x0, 0xFF) }
                offset(1, 1) {
                    area(PANEL_W - 2, PANEL_H - 2) { rect(0x2A1A10, 0xFF) }
                }

                area(PANEL_W, TITLE_H) { rect(0x41261b, 0xFF) }
                textCenteredColored(PANEL_W / 2, (TITLE_H - 8) / 2, title, 0xFFFFE58A.toInt())

                offset(COL_PAD, TITLE_H + 4) {
                    textShadowed(
                        0, 0,
                        Component.translatable("bountiful.editor.picker.available", "Available"),
                        0xFFD9C0A3.toInt()
                    )
                    textShadowed(
                        COL_W + COL_GAP, 0,
                        Component.translatable("bountiful.editor.picker.active", "Active"),
                        0xFFD9C0A3.toInt()
                    )
                }

                val listY = TITLE_H + 16
                offset(COL_PAD, listY) {
                    area(COL_W, LIST_H) { rect(0x1c100a, 0xDD) }
                    widget(availableList)
                    offset(COL_W + 1, 0) { widget(availableScroll) }
                }
                offset(COL_PAD + COL_W + COL_GAP, listY) {
                    area(COL_W, LIST_H) { rect(0x1c100a, 0xDD) }
                    widget(activeList)
                    offset(COL_W + 1, 0) { widget(activeScroll) }
                }

                val btnY = PANEL_H - BTN_H - 6
                val doneX = PANEL_W - BTN_W - 8
                offset(doneX, btnY) {
                    area(BTN_W, BTN_H) {
                        reactWith(doneReactor)
                        rect(0x1f3a1c, 0xFF)
                        onHover { rect(0xFFFFFF, 0x22) }
                        textCenteredColored(
                            BTN_W / 2,
                            (BTN_H - 8) / 2,
                            Component.translatable("bountiful.editor.btn.done", "Done"),
                            0xFFD7F0B9.toInt()
                        )
                    }
                }
            }
        }
    }

    private fun KGuiDsl.drawRow(item: String, added: Boolean) {
        area(COL_W, ROW_H) {
            rect(if (added) 0x41261b else 0x1c100a, 0xCC)
            onHover { rect(0xFFFFFF, 0x22) }
            textShadowed(
                3, (ROW_H - 8) / 2,
                textLiteral(item),
                if (added) 0xFFFFE58A.toInt() else 0xFFD9C0A3.toInt()
            )
        }
    }

    private fun clickAvailable(ry: Int) {
        val row = ry / ROW_H
        val listNow = availableItems()
        val rangeList = availableList.shownRange.toList()
        val idx = rangeList.getOrNull(row) ?: return
        val item = listNow.getOrNull(idx) ?: return
        selected.add(item)
    }

    private fun clickActive(ry: Int) {
        val row = ry / ROW_H
        val listNow = activeItems()
        val rangeList = activeList.shownRange.toList()
        val idx = rangeList.getOrNull(row) ?: return
        val item = listNow.getOrNull(idx) ?: return
        selected.remove(item)
    }

    private fun finish() {
        if (closed) return
        closed = true
        onDone(allSorted.filter { it in selected })
    }

    companion object {
        private const val PANEL_W = 300
        private const val PANEL_H = 160
        private const val TITLE_H = 14
        private const val COL_PAD = 8
        private const val COL_W = 130
        private const val COL_GAP = 14
        private const val ROW_H = 12
        private const val ROWS_SHOWN = 9
        private const val LIST_H = ROWS_SHOWN * ROW_H
        private const val SCROLL_KNOB_H = 27
        private const val BTN_W = 48
        private const val BTN_H = 14
        private val SCROLLER = Identifier.parse("container/villager/scroller")
    }
}
