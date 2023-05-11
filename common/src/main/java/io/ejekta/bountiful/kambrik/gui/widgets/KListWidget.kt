package io.ejekta.bountiful.kambrik.gui.widgets

import io.ejekta.bountiful.kambrik.gui.KGuiDsl
import io.ejekta.bountiful.kambrik.gui.KWidget
import io.ejekta.bountiful.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.gui.screen.Screen
import kotlin.math.max
import kotlin.math.min

open class KListWidget<T>(
    private val items: () -> List<T>,
    val itemWidth: Int,
    val itemHeight: Int,
    private val shown: Int,
    private val orientation: Orientation = Orientation.VERTICAL,
    private val mode: Mode = Mode.MULTI,
    val onDrawItemFunc: KGuiDsl.(
        listWidget: KListWidget<T>,
        item: T,
        selected: Boolean
    ) -> Unit = { _, _, _ -> }
) : KWidget {

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

    enum class Mode {
        SINGLE,
        MULTI,
        TOGGLE
    }

    private var scrollBar: KScrollbar? = null

    fun attachScrollbar(bar: KScrollbar) {
        scrollBar = bar
    }

    // We use a map for lookup performance
    private var internalSelected = mutableMapOf<T, Boolean>()
    private var lastSelectedIndex: Int? = null

    val reactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            val itemRenderIndex = when (orientation) {
                Orientation.HORIZONTAL -> relX / itemWidth
                Orientation.VERTICAL -> relY / itemHeight
            }
            val itemListIndex = shownRange.toList().getOrNull(itemRenderIndex)

            val allItems = items()

            // If holding shift. we can select multiple
            if (Screen.hasShiftDown() && mode == Mode.MULTI && lastSelectedIndex != null && itemListIndex != null) {
                lastSelectedIndex?.let {
                    val selectedRange = min(it, itemListIndex)..max(it, itemListIndex)
                    val selectedItems = selectedRange.toList().mapNotNull { i -> allItems.getOrNull(i) }
                    select(selectedItems)
                }
            } else {
                itemListIndex?.let {
                    val item = allItems[it]
                    select(item)
                    lastSelectedIndex = itemListIndex
                }
            }
        }

        onMouseScrolled = { _, _, amount ->
            scrollBar?.scroll(-amount / max(1, items().size))
        }
    }

    val selected: List<T>
        get() = internalSelected.keys.toList()

    fun select(vararg items: T) {
        select(items.toList())
    }

    fun select(items: List<T>) {

        if (!Screen.hasControlDown() && mode != Mode.TOGGLE) {
            internalSelected.clear()
        }

        when (mode) {
            Mode.SINGLE -> { // Select last item in list
                if (items.isNotEmpty()) {
                    internalSelected.clear()
                    internalSelected[items.last()] = true
                }
            }
            Mode.MULTI -> {
                for (item in items) {
                    internalSelected[item] = true
                }
            }
            Mode.TOGGLE -> {
                for (item in items.toSet()) {
                    if (item in internalSelected) {
                        internalSelected.remove(item)
                    } else {
                        internalSelected[item] = true
                    }
                }
            }
        }

    }

    override val height: Int
        get() = when (orientation) {
            Orientation.HORIZONTAL -> itemHeight
            Orientation.VERTICAL -> items().size * itemHeight
        }

    override val width: Int
        get() = when (orientation) {
            Orientation.HORIZONTAL -> items().size * itemWidth
            Orientation.VERTICAL -> itemWidth
        }

    val shownRange: IntRange
        get() = (scrollBar?.getIndices(items().size, shown) ?: (0 until shown))

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area {
            reactWith(reactor)
            dsl {
                val toIterate = items()
                shownRange.forEachIndexed { index, rowNumToShow ->
                    val offX = when(orientation) {
                        Orientation.HORIZONTAL -> index * itemWidth
                        Orientation.VERTICAL -> 0
                    }
                    val offY = when(orientation) {
                        Orientation.HORIZONTAL -> 0
                        Orientation.VERTICAL -> index * itemHeight
                    }
                    offset(offX, offY) {
                        onDrawItem(this, toIterate[rowNumToShow])
                    }
                }
            }
        }
    }

    open fun onDrawItem(dsl: KGuiDsl, item: T) {
        dsl.onDrawItemFunc(this, item, item in selected)
    }
}