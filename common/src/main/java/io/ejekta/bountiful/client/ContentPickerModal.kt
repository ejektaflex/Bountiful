package io.ejekta.bountiful.client

import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

/** Picker modal for entry Content — shows an item grid, tag grid, or registry list. */
class ContentPickerModal(
    val kind: Kind,
    private val searchProvider: () -> String = { "" },
    private val onDone: (String?) -> Unit
) {
    enum class Kind { ITEM, ITEM_TAG, ENTITY, CRITERIA }

    val hasSearch: Boolean get() = kind == Kind.ITEM || kind == Kind.ITEM_TAG

    private var closed = false

    private val title: Component = when (kind) {
        Kind.ITEM -> Component.translatable("bountiful.editor.picker.title.item", "Pick an Item")
        Kind.ITEM_TAG -> Component.translatable("bountiful.editor.picker.title.item_tag", "Pick an Item Tag")
        Kind.ENTITY -> Component.translatable("bountiful.editor.picker.title.entity", "Pick an Entity")
        Kind.CRITERIA -> Component.translatable("bountiful.editor.picker.title.criteria", "Pick a Criteria")
    }

    private val itemListAll: List<Item> by lazy {
        BuiltInRegistries.ITEM.filter { it != Items.AIR }.sortedBy { BuiltInRegistries.ITEM.getKey(it).toString() }
    }

    private val itemTagListAll: List<Identifier> by lazy {
        BuiltInRegistries.ITEM.getTags().toList()
            .mapNotNull { entry -> runCatching { entry.key().location() }.getOrNull() }
            .distinct()
            .sortedBy { it.toString() }
    }

    private val entityList: List<String> by lazy {
        BuiltInRegistries.ENTITY_TYPE.keySet().map { it.toString() }.sorted()
    }

    private val criteriaList: List<String> by lazy {
        BuiltInRegistries.TRIGGER_TYPES.keySet().map { it.toString() }.sorted()
    }

    private fun filteredItemList(): List<Item> {
        val q = searchProvider().trim().lowercase()
        if (q.isEmpty()) return itemListAll
        return itemListAll.filter { item ->
            val id = BuiltInRegistries.ITEM.getKey(item).toString()
            id.contains(q, ignoreCase = true) ||
                ItemStack(item).hoverName.string.contains(q, ignoreCase = true)
        }
    }

    private fun filteredItemTagList(): List<Identifier> {
        val q = searchProvider().trim().lowercase()
        if (q.isEmpty()) return itemTagListAll
        return itemTagListAll.filter { it.toString().contains(q, ignoreCase = true) }
    }

    private val rowCount: Int
        get() = when (kind) {
            Kind.ITEM -> (filteredItemList().size + ITEM_COLS - 1) / ITEM_COLS
            Kind.ITEM_TAG -> (filteredItemTagList().size + TAG_COLS - 1) / TAG_COLS
            Kind.ENTITY -> entityList.size
            Kind.CRITERIA -> criteriaList.size
        }

    private val visibleRows: Int
        get() = when (kind) {
            Kind.ITEM -> ITEM_VISIBLE_ROWS
            Kind.ITEM_TAG -> TAG_VISIBLE_ROWS
            else -> LIST_VISIBLE_ROWS
        }

    private val rowHeight: Int
        get() = when (kind) {
            Kind.ITEM -> ITEM_CELL
            Kind.ITEM_TAG -> TAG_CELL
            else -> LIST_ROW_H
        }

    private val gridHeight: Int get() = visibleRows * rowHeight

    private val scrollbar = KScrollbarVertical(gridHeight, 6, SCROLL_KNOB, SCROLLER, 0x0)

    private fun firstRow(): Int {
        if (rowCount <= visibleRows) return 0
        val maxStart = rowCount - visibleRows
        return (scrollbar.percent * maxStart).toInt().coerceIn(0, maxStart)
    }

    private val backdropReactor = MouseReactor().apply {
        canPassThrough = { false }
        onClickDown = { _, _, _ -> /* swallow */ }
    }

    private val gridReactor = MouseReactor().apply {
        canPassThrough = { true }
        onClickDown = { rx, ry, _ -> handleGridClick(rx, ry) }
        onMouseScrolled = { _, _, _, v ->
            if (rowCount > visibleRows) {
                // Fixed 5% per tick. A percent scaled by rowCount rounds to 0 pixels for long lists.
                scrollbar.scroll(-v * 0.05)
            }
        }
    }

    private val cancelReactor = MouseReactor().apply {
        onClickDown = { _, _, _ -> close(null) }
    }

    fun drawInto(dsl: KGuiDsl) {
        if (closed) return
        with(dsl) {
            area(EditorScreen.GUI_WIDTH, EditorScreen.GUI_HEIGHT) {
                reactWith(backdropReactor)
                rect(0x000000, 0xC0)
            }

            val panelX = (EditorScreen.GUI_WIDTH - PANEL_W) / 2
            val panelY = (EditorScreen.GUI_HEIGHT - PANEL_H) / 2
            offset(panelX, panelY) {
                area(PANEL_W, PANEL_H) { rect(0x2A1A10, 0xFF) }
                offset(1, 1) { area(PANEL_W - 2, PANEL_H - 2) { rect(0x3c241a, 0xFF) } }

                area(PANEL_W, TITLE_H) { rect(0x41261b, 0xFF) }
                textCenteredColored(PANEL_W / 2, (TITLE_H - 8) / 2, title, 0xFFFFE58A.toInt())

                val gridX = (PANEL_W - gridPixelWidth()) / 2
                val gridY = TITLE_H + (if (hasSearch) SEARCH_H + 8 else 6)
                offset(gridX, gridY) {
                    area(gridPixelWidth(), gridHeight) {
                        rect(0x1c100a, 0xFF)
                        reactWith(gridReactor)
                    }
                    drawContents()
                }

                // Scrollbar to the right of the grid
                if (rowCount > visibleRows) {
                    offset(gridX + gridPixelWidth() + 2, gridY) {
                        widget(scrollbar)
                    }
                }

                // Cancel button bottom-right
                offset(PANEL_W - BTN_W - 6, PANEL_H - BTN_H - 6) {
                    area(BTN_W, BTN_H) {
                        reactWith(cancelReactor)
                        rect(0x5a1a1a, 0xFF)
                        onHover { rect(0xFFFFFF, 0x22) }
                        textCenteredColored(
                            BTN_W / 2, (BTN_H - 8) / 2,
                            Component.translatable("bountiful.editor.btn.cancel", "Cancel"),
                            0xFFF0B9B9.toInt()
                        )
                    }
                }
            }
        }
    }

    private fun gridPixelWidth(): Int = when (kind) {
        Kind.ITEM -> ITEM_COLS * ITEM_CELL
        Kind.ITEM_TAG -> TAG_COLS * TAG_CELL
        Kind.ENTITY, Kind.CRITERIA -> LIST_W
    }

    private fun KGuiDsl.drawContents() {
        val startRow = firstRow()
        when (kind) {
            Kind.ITEM -> drawItemGrid(filteredItemList(), startRow)
            Kind.ITEM_TAG -> drawTagGrid(filteredItemTagList(), startRow)
            Kind.ENTITY -> drawRegistryList(entityList, startRow)
            Kind.CRITERIA -> drawRegistryList(criteriaList, startRow)
        }
    }

    private fun KGuiDsl.drawItemGrid(list: List<Item>, startRow: Int) {
        for (r in 0 until visibleRows) {
            val gridRow = startRow + r
            for (c in 0 until ITEM_COLS) {
                val idx = gridRow * ITEM_COLS + c
                val item = list.getOrNull(idx) ?: return
                val stack = ItemStack(item)
                offset(c * ITEM_CELL + 1, r * ITEM_CELL + 1) {
                    area(ITEM_CELL - 2, ITEM_CELL - 2) {
                        rect(0x0, 0x66)
                        onHover {
                            rect(0xFFFFFF, 0x33)
                            tooltip(listOf(stack.hoverName))
                        }
                    }
                    itemStackIcon(stack, 0, 0)
                }
            }
        }
    }

    private fun KGuiDsl.drawTagGrid(list: List<Identifier>, startRow: Int) {
        val world = Minecraft.getInstance().level
        val gameTime = world?.gameTime ?: 0L
        val frame = (gameTime / 30L).toInt()
        for (r in 0 until visibleRows) {
            val gridRow = startRow + r
            for (c in 0 until TAG_COLS) {
                val idx = gridRow * TAG_COLS + c
                val tagId = list.getOrNull(idx) ?: return
                val items = runCatching { getTagItems(getTagItemKey(tagId)) }.getOrElse { emptyList() }
                val stack = if (items.isEmpty()) ItemStack(Items.BARRIER) else ItemStack(items[frame % items.size])
                offset(c * TAG_CELL + 1, r * TAG_CELL + 1) {
                    area(TAG_CELL - 2, TAG_CELL - 2) {
                        rect(0x0, 0x66)
                        onHover {
                            rect(0xFFFFFF, 0x33)
                            tooltip(listOf(textLiteral("#$tagId")))
                        }
                    }
                    itemStackIcon(stack, 2, 2)
                }
            }
        }
    }

    private fun KGuiDsl.drawRegistryList(list: List<String>, startRow: Int) {
        for (r in 0 until visibleRows) {
            val idx = startRow + r
            val value = list.getOrNull(idx) ?: return
            offset(0, r * LIST_ROW_H) {
                area(LIST_W, LIST_ROW_H) {
                    rect(0x1c100a, 0xFF)
                    onHover { rect(0xFFFFFF, 0x22) }
                    textShadowed(4, (LIST_ROW_H - 8) / 2, textLiteral(value), 0xFFD9C0A3.toInt())
                }
            }
        }
    }

    private fun handleGridClick(relX: Int, relY: Int) {
        val startRow = firstRow()
        when (kind) {
            Kind.ITEM -> {
                val col = relX / ITEM_CELL
                val row = relY / ITEM_CELL
                if (col !in 0 until ITEM_COLS) return
                val list = filteredItemList()
                val idx = (startRow + row) * ITEM_COLS + col
                val item = list.getOrNull(idx) ?: return
                close(BuiltInRegistries.ITEM.getKey(item).toString())
            }
            Kind.ITEM_TAG -> {
                val col = relX / TAG_CELL
                val row = relY / TAG_CELL
                if (col !in 0 until TAG_COLS) return
                val list = filteredItemTagList()
                val idx = (startRow + row) * TAG_COLS + col
                val tagId = list.getOrNull(idx) ?: return
                close(tagId.toString())
            }
            Kind.ENTITY -> {
                val row = relY / LIST_ROW_H
                val idx = startRow + row
                val value = entityList.getOrNull(idx) ?: return
                close(value)
            }
            Kind.CRITERIA -> {
                val row = relY / LIST_ROW_H
                val idx = startRow + row
                val value = criteriaList.getOrNull(idx) ?: return
                close(value)
            }
        }
    }

    private fun close(result: String?) {
        if (closed) return
        closed = true
        onDone(result)
    }

    companion object {
        const val PANEL_W = 260
        const val PANEL_H = 170
        const val TITLE_H = 14
        private const val BTN_W = 50
        private const val BTN_H = 14

        private const val ITEM_COLS = 12
        private const val ITEM_CELL = 18
        private const val ITEM_VISIBLE_ROWS = 6

        private const val TAG_COLS = 9
        private const val TAG_CELL = 22
        private const val TAG_VISIBLE_ROWS = 4

        const val SEARCH_H = 14

        private const val LIST_W = 220
        private const val LIST_ROW_H = 12
        private const val LIST_VISIBLE_ROWS = 10

        private const val SCROLL_KNOB = 27
        private val SCROLLER = Identifier.parse("container/villager/scroller")
    }
}
