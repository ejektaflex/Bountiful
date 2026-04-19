package io.ejekta.bountiful.client

import io.ejekta.bountiful.client.widgets.EditorTabButton
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.screen.KambrikContainerScreen
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

class EditorScreen(handler: AbstractContainerMenu, inventory: Inventory, title: Component) :
    KambrikContainerScreen<AbstractContainerMenu>(handler, inventory, title, GUI_WIDTH, GUI_HEIGHT) {

    enum class Tab { POOLS, DECREES }

    private var activeTab: Tab = Tab.POOLS
    private val decreePanel = DecreeEditorPanel(this)
    private val poolPanel = PoolEditorPanel(this)

    private val tabButtons = listOf(
        EditorTabButton(
            width = TAB_WIDTH,
            label = Component.translatable("bountiful.editor.tab.pools", "Pools"),
            isActive = { activeTab == Tab.POOLS },
            onSelect = { selectTab(Tab.POOLS) }
        ),
        EditorTabButton(
            width = TAB_WIDTH,
            label = Component.translatable("bountiful.editor.tab.decrees", "Decrees"),
            isActive = { activeTab == Tab.DECREES },
            onSelect = { selectTab(Tab.DECREES) }
        )
    )

    private val bgGui = kambrikGui {
        area(GUI_WIDTH, GUI_HEIGHT) { rect(0x0D0602, 0xF5) }
        rect(2, TAB_STRIP_HEIGHT, GUI_WIDTH - 4, GUI_HEIGHT - TAB_STRIP_HEIGHT - 2, 0x2A1A10, 0xF0)
    }

    private val fgGui = kambrikGui {
        offset(4, 1) {
            for (i in tabButtons.indices) {
                offset(i * (TAB_WIDTH + 2), 0) { widget(tabButtons[i]) }
            }
        }
        textCenteredColored(
            GUI_WIDTH / 2,
            (TAB_STRIP_HEIGHT - 8) / 2,
            title,
            0xFFEADAB5.toInt()
        )
        when (activeTab) {
            Tab.DECREES -> decreePanel.drawInto(this)
            Tab.POOLS -> poolPanel.drawInto(this)
        }
    }

    override fun init() {
        super.init()
        leftPos = (width - GUI_WIDTH) / 2
        topPos = (height - GUI_HEIGHT) / 2
        decreePanel.build()
        poolPanel.build()
        applyTabVisibility()
    }

    override fun onDrawBackground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        bgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    private fun selectTab(tab: Tab) {
        if (activeTab == tab) return
        activeTab = tab
        applyTabVisibility()
    }

    private fun applyTabVisibility() {
        decreePanel.setVisible(activeTab == Tab.DECREES)
        poolPanel.setVisible(activeTab == Tab.POOLS)
    }

    val leftPosPublic: Int get() = leftPos
    val topPosPublic: Int get() = topPos

    override fun keyPressed(event: KeyEvent): Boolean {
        // When an EditBox has focus, a printable key (default 'E') would otherwise match
        // `keyInventory` inside AbstractContainerScreen.keyPressed and close the screen.
        // Swallow that here so charTyped can deliver the character to the text field instead.
        val f = this.focused
        if (f is EditBox && f.canConsumeInput() && minecraft.options.keyInventory.matches(event)) {
            return true
        }
        return super.keyPressed(event)
    }

    fun bodyX(): Int = leftPos
    fun bodyY(): Int = topPos + TAB_STRIP_HEIGHT

    // Exposed for companion panels that need to register MC-native widgets.
    val fontPublic: Font get() = font

    fun <T> addPanelWidget(widget: T): T where T : GuiEventListener, T : Renderable, T : NarratableEntry =
        addRenderableWidget(widget)

    companion object {
        const val GUI_WIDTH = 348
        const val GUI_HEIGHT = 188
        const val TAB_STRIP_HEIGHT = 18
        const val TAB_WIDTH = 60
    }
}
