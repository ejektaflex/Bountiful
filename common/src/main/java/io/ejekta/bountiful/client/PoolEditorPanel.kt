package io.ejekta.bountiful.client

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.editor.PoolEntryEditorPayload
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.messages.DeletePoolEdit
import io.ejekta.bountiful.messages.DeletePoolEntryEdit
import io.ejekta.bountiful.messages.SavePoolEdit
import io.ejekta.bountiful.messages.SavePoolEntryEdit
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.gui.draw.widgets.KListWidget
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

internal data class PoolEntryDraft(
    var key: String,
    val originalKey: String,
    var typeId: String,
    var rarity: BountyRarity,
    var content: String,
    var amountMin: Int,
    var amountMax: Int,
    var unitWorth: Double,
    var weightMult: Double,
    var timeMult: Double,
    var repRequired: Double,
    var name: String,
    var mystery: Boolean,
    val markers: MutableList<String>,
    val forbidMarkers: MutableList<String>,
    val biomes: MutableList<String>,
    val existingComponentsJson: String?,
    val existingConditionsJson: String?,
    val isNew: Boolean = false
) {
    companion object {
        fun from(entry: PoolEntry): PoolEntryDraft {
            val key = entry.id.substringAfter('.')
            // Biomes may be absent, a string, or an array — normalize to a list of strings.
            val biomeList: MutableList<String> = when (val b = entry.biomes) {
                null -> mutableListOf()
                is com.google.gson.JsonArray -> b.mapNotNull { el ->
                    (el as? com.google.gson.JsonPrimitive)?.takeIf { it.isString }?.asString
                }.toMutableList()
                is com.google.gson.JsonPrimitive -> if (b.isString) mutableListOf(b.asString) else mutableListOf()
                else -> mutableListOf()
            }
            return PoolEntryDraft(
                key = key,
                originalKey = key,
                typeId = entry.type.toString(),
                rarity = entry.rarity,
                content = entry.content,
                amountMin = entry.amount.min,
                amountMax = entry.amount.max,
                unitWorth = entry.unitWorth,
                weightMult = entry.weightMult,
                timeMult = entry.timeMult,
                repRequired = entry.repRequired,
                name = entry.name.orEmpty(),
                mystery = entry.mystery,
                markers = entry.markers.sorted().toMutableList(),
                forbidMarkers = entry.forbidMarkers.sorted().toMutableList(),
                biomes = biomeList,
                existingComponentsJson = entry.components?.toString(),
                existingConditionsJson = entry.conditions?.toString()
            )
        }

        fun empty(): PoolEntryDraft = PoolEntryDraft(
            key = "new_entry",
            originalKey = "new_entry",
            typeId = BountyTypeRegistry.ITEM.id.toString(),
            rarity = BountyRarity.COMMON,
            content = "minecraft:stone",
            amountMin = 1,
            amountMax = 1,
            unitWorth = 100.0,
            weightMult = 1.0,
            timeMult = 1.0,
            repRequired = 0.0,
            name = "",
            mystery = false,
            markers = mutableListOf(),
            forbidMarkers = mutableListOf(),
            biomes = mutableListOf(),
            existingComponentsJson = null,
            existingConditionsJson = null,
            isNew = true
        )
    }
}

internal data class PoolDraft(
    var id: String,
    val originalId: String,
    val entries: MutableList<PoolEntryDraft>,
    val isNew: Boolean = false
) {
    companion object {
        fun from(p: Pool): PoolDraft {
            // Decode entries directly from raw content JSON so we see pre-normalization weightMult.
            // (Pool.items has already been through finishMergedSetup() which divides weightMult by pool size.)
            val rawEntries = p.content.mapNotNull { (key, json) ->
                if (json == null) return@mapNotNull null
                val raw = runCatching {
                    JsonFormats.Config.dynamicDecodeFromString(json.toString(), PoolEntry.serializer())
                }.getOrNull() ?: return@mapNotNull null
                raw.id = "${p.id}.$key"
                PoolEntryDraft.from(raw)
            }.sortedBy { it.key }.toMutableList()
            return PoolDraft(
                id = p.id,
                originalId = p.id,
                entries = rawEntries,
                isNew = false
            )
        }
    }
}

class PoolEditorPanel(private val parent: EditorScreen) {

    enum class DetailTab { BASIC, ADVANCED, MARKERS }

    private enum class PickerField { MARKERS, FORBID_MARKERS, BIOMES }

    private val pools: MutableList<PoolDraft> = BountifulContent.Pools
        .sortedBy { it.id }
        .map { PoolDraft.from(it) }
        .toMutableList()

    private var selectedPoolIdx: Int = if (pools.isNotEmpty()) 0 else -1
    private var selectedEntryIdx: Int = -1
    private var detailTab: DetailTab = DetailTab.BASIC
    private var panelVisible: Boolean = false
    private var newPoolModalOpen: Boolean = false
    private var activeContentModal: ContentPickerModal? = null
    private var activePickerModal: EditorPickerModal? = null

    // Basic tab MC widgets
    private lateinit var keyBox: EditBox
    private lateinit var contentBox: EditBox
    private lateinit var minBox: EditBox
    private lateinit var maxBox: EditBox
    private lateinit var worthBox: EditBox

    // Advanced tab MC widgets
    private lateinit var weightBox: EditBox
    private lateinit var timeBox: EditBox
    private lateinit var repBox: EditBox
    private lateinit var nameBox: EditBox

    private lateinit var saveEntryBtn: Button

    // New-pool naming modal widgets
    private lateinit var newPoolIdBox: EditBox
    private lateinit var newPoolCreateBtn: Button
    private lateinit var newPoolCancelBtn: Button

    // Search box for content picker modals (ITEM / ITEM_TAG only)
    private lateinit var contentSearchBox: EditBox
    private var contentSearchText: String = ""

    private val poolListScroller = KScrollbarVertical(LIST_H, 6, SCROLL_KNOB, SCROLLER, 0x0)
    private val entryListScroller = KScrollbarVertical(LIST_H, 6, SCROLL_KNOB, SCROLLER, 0x0)

    private val poolList = KListWidget(
        items = { pools },
        itemWidth = POOL_COL_W,
        itemHeight = ROW_H,
        shown = LIST_ROWS,
        orientation = KListWidget.Orientation.VERTICAL,
        mode = KListWidget.Mode.SINGLE,
        onDrawItemFunc = { _, pool, _ ->
            val idx = pools.indexOf(pool)
            val isSel = idx == selectedPoolIdx
            area(POOL_COL_W, ROW_H) {
                if (isSel) rect(0x41261b, 0xCC) else rect(0x1c100a, 0x88)
                onHover { if (!isSel) rect(0xFFFFFF, 0x22) }
                val label = truncated(pool.id + if (pool.isNew) " *" else "", POOL_COL_W - 6)
                val color = if (isSel) 0xFFFFE58A.toInt() else 0xFFD9C0A3.toInt()
                textShadowed(3, (ROW_H - 8) / 2, textLiteral(label), color)
            }
        }
    ).apply {
        attachScrollbar(poolListScroller)
        reactor.canPassThrough = { true }
        reactor.onClickDown = { _, ry, _ ->
            val row = ry / ROW_H
            val idx = shownRange.toList().getOrNull(row)
            if (idx != null && idx in pools.indices) {
                selectedPoolIdx = idx
                selectedEntryIdx = -1
                loadEntryToForm()
            }
        }
    }

    private val entryList = KListWidget(
        items = { currentPool()?.entries ?: emptyList() },
        itemWidth = ENTRY_COL_W,
        itemHeight = ROW_H,
        shown = LIST_ROWS,
        orientation = KListWidget.Orientation.VERTICAL,
        mode = KListWidget.Mode.SINGLE,
        onDrawItemFunc = { _, entry, _ ->
            val idx = currentPool()?.entries?.indexOf(entry) ?: -1
            val isSel = idx == selectedEntryIdx
            area(ENTRY_COL_W, ROW_H) {
                if (isSel) rect(0x41261b, 0xCC) else rect(0x1c100a, 0x88)
                onHover { if (!isSel) rect(0xFFFFFF, 0x22) }
                val label = truncated(entry.key + if (entry.isNew) " *" else "", ENTRY_COL_W - 6)
                val color = if (isSel) 0xFFFFE58A.toInt() else rarityColor(entry.rarity)
                textShadowed(3, (ROW_H - 8) / 2, textLiteral(label), color)
            }
        }
    ).apply {
        attachScrollbar(entryListScroller)
        reactor.canPassThrough = { true }
        reactor.onClickDown = { _, ry, _ ->
            currentPool()?.entries?.let { entries ->
                val idx = shownRange.toList().getOrNull(ry / ROW_H)
                if (idx != null && idx in entries.indices) {
                    syncFormToEntry()
                    selectedEntryIdx = idx
                    loadEntryToForm()
                }
            }
        }
    }

    private val newPoolRow = ActionRow(
        width = POOL_COL_W,
        fillColor = 0x1f3a1c,
        textColor = 0xFFD7F0B9.toInt(),
        text = Component.translatable("bountiful.editor.pool.new", "+ New Pool")
    ) { openNewPoolModal() }

    private val deletePoolRow = ActionRow(
        width = POOL_COL_W,
        fillColor = 0x5a1a1a,
        textColor = 0xFFF0B9B9.toInt(),
        text = Component.translatable("bountiful.editor.pool.delete", "- Delete Pool"),
        isEnabled = { currentPool() != null }
    ) { deleteSelectedPool() }

    private val newEntryRow = ActionRow(
        width = ENTRY_COL_W,
        fillColor = 0x1f3a1c,
        textColor = 0xFFD7F0B9.toInt(),
        text = Component.translatable("bountiful.editor.entry.new", "+ New Entry"),
        isEnabled = { currentPool() != null }
    ) { createNewEntry() }

    private val deleteEntryRow = ActionRow(
        width = ENTRY_COL_W,
        fillColor = 0x5a1a1a,
        textColor = 0xFFF0B9B9.toInt(),
        text = Component.translatable("bountiful.editor.entry.delete", "- Delete Entry"),
        isEnabled = { currentEntry() != null && currentEntry()?.isNew != true }
    ) { deleteSelectedEntry() }

    private val basicSubTab = SubTabButton(
        width = SUBTAB_W,
        label = Component.translatable("bountiful.editor.tab.basic", "Basic"),
        isActive = { detailTab == DetailTab.BASIC },
        onSelect = { setDetailTab(DetailTab.BASIC) }
    )

    private val advSubTab = SubTabButton(
        width = SUBTAB_W,
        label = Component.translatable("bountiful.editor.tab.advanced", "Adv"),
        isActive = { detailTab == DetailTab.ADVANCED },
        onSelect = { setDetailTab(DetailTab.ADVANCED) }
    )

    private val markersSubTab = SubTabButton(
        width = SUBTAB_W,
        label = Component.translatable("bountiful.editor.tab.markers", "Markers"),
        isActive = { detailTab == DetailTab.MARKERS },
        onSelect = { setDetailTab(DetailTab.MARKERS) }
    )

    private val markersPicker = ContentPickerRow(
        width = FIELD_W,
        textProvider = { (currentEntry()?.markers?.size ?: 0).let { "$it selected" } },
        onClick = { openStringPicker(PickerField.MARKERS) }
    )

    private val forbidMarkersPicker = ContentPickerRow(
        width = FIELD_W,
        textProvider = { (currentEntry()?.forbidMarkers?.size ?: 0).let { "$it selected" } },
        onClick = { openStringPicker(PickerField.FORBID_MARKERS) }
    )

    private val biomesPicker = ContentPickerRow(
        width = FIELD_W,
        textProvider = { (currentEntry()?.biomes?.size ?: 0).let { "$it selected" } },
        onClick = { openStringPicker(PickerField.BIOMES) }
    )

    private val typeCycle = CycleButton(
        width = CYCLE_W,
        options = { BountyTypeRegistry.map { it.id.path } },
        currentValue = { currentEntry()?.typeId?.let { Identifier.parse(it).path } ?: "" },
        onCycle = { next ->
            currentEntry()?.let { entry ->
                entry.typeId = BountyTypeRegistry.first { it.id.path == next }.id.toString()
                // Content doesn't carry across types (item id, entity id, command string, etc are unrelated).
                entry.content = ""
                if (::contentBox.isInitialized) contentBox.setValue("")
                applyMcWidgetVisibility()
            }
        }
    )

    private val contentPicker = ContentPickerRow(
        width = FIELD_W,
        textProvider = { currentEntry()?.content ?: "" },
        onClick = { openContentPicker() }
    )

    private val rarityCycle = CycleButton(
        width = CYCLE_W,
        options = { BountyRarity.entries.map { it.name.lowercase() } },
        currentValue = { currentEntry()?.rarity?.name?.lowercase() ?: "" },
        onCycle = { next -> currentEntry()?.rarity = BountyRarity.valueOf(next.uppercase()) },
        colorProvider = { value ->
            val r = runCatching { BountyRarity.valueOf(value.uppercase()) }.getOrNull()
            r?.color?.color?.let { 0xFF000000.toInt() or (it and 0xFFFFFF) } ?: COLOR_TEXT
        }
    )

    private val mysteryCheckbox = CheckboxReactor { currentEntry()?.let { it.mystery = !it.mystery } }

    fun build() {
        val bx = parent.bodyX()
        val by = parent.bodyY()
        val detailX = bx + DETAIL_X
        val detailY = by + DETAIL_Y

        // Basic tab fields
        keyBox = makeEditBox(detailX + FIELD_X, detailY + row(2), FIELD_W, "bountiful.editor.field.key")
        contentBox = makeEditBox(detailX + FIELD_X, detailY + row(4), FIELD_W, "bountiful.editor.field.content")
        minBox = makeEditBox(detailX + FIELD_X, detailY + row(5), SMALL_W, "bountiful.editor.field.amount_min")
        maxBox = makeEditBox(detailX + FIELD_X + SMALL_W + 4, detailY + row(5), SMALL_W, "bountiful.editor.field.amount_max")
        worthBox = makeEditBox(detailX + FIELD_X, detailY + row(6), SMALL_W * 2 + 4, "bountiful.editor.field.worth")

        // Advanced tab fields
        weightBox = makeEditBox(detailX + FIELD_X, detailY + row(1), FIELD_W, "bountiful.editor.field.weight")
        timeBox = makeEditBox(detailX + FIELD_X, detailY + row(2), FIELD_W, "bountiful.editor.field.time")
        repBox = makeEditBox(detailX + FIELD_X, detailY + row(3), FIELD_W, "bountiful.editor.field.rep")
        nameBox = makeEditBox(detailX + FIELD_X, detailY + row(4), FIELD_W, "bountiful.editor.field.name")

        for (w in allMcWidgets()) parent.addPanelWidget(w)

        saveEntryBtn = Button.builder(
            Component.translatable("bountiful.editor.btn.save", "Save")
        ) { saveSelectedEntry() }
            .bounds(detailX + DETAIL_W - SAVE_W, detailY + SAVE_Y, SAVE_W, SAVE_H)
            .build()
        parent.addPanelWidget(saveEntryBtn)

        // New-pool modal widgets
        val midX = parent.leftPosPublic + EditorScreen.GUI_WIDTH / 2
        val midY = parent.topPosPublic + EditorScreen.GUI_HEIGHT / 2
        newPoolIdBox = EditBox(
            parent.fontPublic,
            midX - 100, midY - 8, 200, 16,
            Component.translatable("bountiful.editor.newpool.hint", "pool id")
        ).apply { setMaxLength(128); setCanLoseFocus(true) }

        newPoolCreateBtn = Button.builder(
            Component.translatable("bountiful.editor.btn.create", "Create")
        ) { confirmNewPool() }
            .bounds(midX + 6, midY + 14, 70, 16)
            .build()

        newPoolCancelBtn = Button.builder(
            Component.translatable("bountiful.editor.btn.cancel", "Cancel")
        ) { closeNewPoolModal() }
            .bounds(midX - 76, midY + 14, 70, 16)
            .build()

        parent.addPanelWidget(newPoolIdBox)
        parent.addPanelWidget(newPoolCreateBtn)
        parent.addPanelWidget(newPoolCancelBtn)

        // Content picker search box (positioned on open)
        contentSearchBox = EditBox(
            parent.fontPublic,
            0, 0,
            ContentPickerModal.PANEL_W - 16,
            ContentPickerModal.SEARCH_H,
            Component.translatable("bountiful.editor.picker.search", "Search…")
        ).apply {
            setMaxLength(64)
            setCanLoseFocus(true)
            setResponder { newText -> contentSearchText = newText }
        }
        parent.addPanelWidget(contentSearchBox)

        loadEntryToForm()
    }

    fun setVisible(visible: Boolean) {
        panelVisible = visible
        if (!::keyBox.isInitialized) return
        applyMcWidgetVisibility()
    }

    fun drawInto(dsl: KGuiDsl) {
        val contentModal = activeContentModal
        if (contentModal != null) {
            contentModal.drawInto(dsl)
            // Search box was already drawn by super.extractRenderState BEFORE the modal covered it.
            // Re-extract its render state on top so it's visible above the modal backdrop.
            if (contentModal.hasSearch && ::contentSearchBox.isInitialized && contentSearchBox.visible) {
                contentSearchBox.extractRenderState(dsl.context, dsl.mouseX, dsl.mouseY, dsl.delta ?: 0f)
            }
            return
        }
        val pickerModal = activePickerModal
        if (pickerModal != null) {
            pickerModal.drawInto(dsl)
            return
        }
        with(dsl) { renderPanelBody() }
    }

    private fun applyMcWidgetVisibility() {
        val anyModalOpen = newPoolModalOpen || activeContentModal != null || activePickerModal != null
        val showPanel = panelVisible && !anyModalOpen
        val basicShown = showPanel && detailTab == DetailTab.BASIC && currentEntry() != null
        val advShown = showPanel && detailTab == DetailTab.ADVANCED && currentEntry() != null

        for (w in basicTabFields()) { w.visible = basicShown; if (!basicShown) w.isFocused = false }
        for (w in advTabFields()) { w.visible = advShown; if (!advShown) w.isFocused = false }

        // Content box only visible when the type is COMMAND (free text). Other types use the picker widget.
        contentBox.visible = basicShown && entryTypeIsCommand()
        if (!contentBox.visible) contentBox.isFocused = false

        saveEntryBtn.visible = showPanel && currentEntry() != null

        val modalShown = panelVisible && newPoolModalOpen
        newPoolIdBox.visible = modalShown
        newPoolCreateBtn.visible = modalShown
        newPoolCancelBtn.visible = modalShown
        if (!modalShown) newPoolIdBox.isFocused = false

        // Search box only while a content picker with search is open
        val cm = activeContentModal
        val searchShown = panelVisible && cm != null && cm.hasSearch
        contentSearchBox.visible = searchShown
        if (!searchShown) contentSearchBox.isFocused = false

        updateFormEnablement()
    }

    private fun basicTabFields(): List<EditBox> = listOf(keyBox, contentBox, minBox, maxBox, worthBox)
    private fun advTabFields(): List<EditBox> = listOf(weightBox, timeBox, repBox, nameBox)
    private fun allMcWidgets(): List<EditBox> = basicTabFields() + advTabFields()

    private fun currentPool(): PoolDraft? = pools.getOrNull(selectedPoolIdx)
    private fun currentEntry(): PoolEntryDraft? = currentPool()?.entries?.getOrNull(selectedEntryIdx)

    private fun entryTypeIsCommand(): Boolean =
        currentEntry()?.typeId == BountyTypeRegistry.COMMAND.id.toString()

    private fun entryPickerKind(): ContentPickerModal.Kind? = when (currentEntry()?.typeId) {
        BountyTypeRegistry.ITEM.id.toString() -> ContentPickerModal.Kind.ITEM
        BountyTypeRegistry.ITEM_TAG.id.toString() -> ContentPickerModal.Kind.ITEM_TAG
        BountyTypeRegistry.ENTITY.id.toString() -> ContentPickerModal.Kind.ENTITY
        BountyTypeRegistry.CRITERIA.id.toString() -> ContentPickerModal.Kind.CRITERIA
        else -> null
    }

    private fun openContentPicker() {
        val kind = entryPickerKind() ?: return
        syncFormToEntry()
        contentSearchText = ""
        if (::contentSearchBox.isInitialized) contentSearchBox.setValue("")
        activeContentModal = ContentPickerModal(
            kind = kind,
            searchProvider = { contentSearchText },
            onDone = { result ->
                if (result != null) {
                    currentEntry()?.content = result
                    if (::contentBox.isInitialized) contentBox.setValue(result)
                }
                activeContentModal = null
                contentSearchText = ""
                if (::contentSearchBox.isInitialized) contentSearchBox.setValue("")
                applyMcWidgetVisibility()
            }
        )
        // Position search box over the modal panel
        if (kind == ContentPickerModal.Kind.ITEM || kind == ContentPickerModal.Kind.ITEM_TAG) {
            val panelX = parent.leftPosPublic + (EditorScreen.GUI_WIDTH - ContentPickerModal.PANEL_W) / 2
            val panelY = parent.topPosPublic + (EditorScreen.GUI_HEIGHT - ContentPickerModal.PANEL_H) / 2
            contentSearchBox.x = panelX + 8
            contentSearchBox.y = panelY + ContentPickerModal.TITLE_H + 4
        }
        applyMcWidgetVisibility()
        if (::contentSearchBox.isInitialized && contentSearchBox.visible) {
            // Both mark the box focused AND tell the screen to route keyboard events to it.
            contentSearchBox.isFocused = true
            parent.setFocused(contentSearchBox)
        }
    }

    private fun openStringPicker(field: PickerField) {
        val entry = currentEntry() ?: return
        syncFormToEntry()
        val title: Component
        val available: List<String>
        val current: MutableList<String>
        when (field) {
            PickerField.MARKERS -> {
                title = Component.translatable("bountiful.editor.picker.title.markers", "Markers")
                available = allKnownMarkers()
                current = entry.markers
            }
            PickerField.FORBID_MARKERS -> {
                title = Component.translatable("bountiful.editor.picker.title.forbid_markers", "Forbid Markers")
                available = allKnownMarkers()
                current = entry.forbidMarkers
            }
            PickerField.BIOMES -> {
                title = Component.translatable("bountiful.editor.picker.title.biomes", "Biomes")
                available = allKnownBiomes()
                current = entry.biomes
            }
        }
        activePickerModal = EditorPickerModal(parent, title, available, current) { newSel ->
            current.clear()
            current.addAll(newSel)
            activePickerModal = null
            applyMcWidgetVisibility()
        }
        applyMcWidgetVisibility()
    }

    private fun allKnownMarkers(): List<String> {
        val markers = mutableSetOf<String>()
        BountifulContent.Pools.forEach { pool ->
            pool.items.forEach { entry ->
                markers.addAll(entry.markers)
                markers.addAll(entry.forbidMarkers)
            }
        }
        // Include markers already on unsaved drafts so the user can see them too.
        pools.forEach { pool ->
            pool.entries.forEach { entry ->
                markers.addAll(entry.markers)
                markers.addAll(entry.forbidMarkers)
            }
        }
        return markers.sorted()
    }

    private fun allKnownBiomes(): List<String> {
        val level = net.minecraft.client.Minecraft.getInstance().level ?: return emptyList()
        val registry = runCatching {
            level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BIOME)
        }.getOrNull() ?: return emptyList()
        val result = mutableListOf<String>()
        registry.listElements().forEach { holder ->
            val id = holder.unwrapKey().map { it.identifier().toString() }.orElse(null)
            if (id != null) result.add(id)
        }
        return result.sorted()
    }

    private fun setDetailTab(tab: DetailTab) {
        if (detailTab == tab) return
        syncFormToEntry()
        detailTab = tab
        loadEntryToForm()
    }

    private fun KGuiDsl.renderPanelBody() {
        offset(0, EditorScreen.TAB_STRIP_HEIGHT) {
            // Pool column
            offset(POOL_X, COL_Y) {
                area(POOL_COL_W, LIST_H) { rect(0x0, 0x55) }
                widget(poolList)
                if (pools.size > LIST_ROWS) offset(POOL_COL_W + 1, 0) { widget(poolListScroller) }
                offset(0, LIST_H + 4) { widget(newPoolRow) }
                offset(0, LIST_H + 4 + ROW_H + 4) { widget(deletePoolRow) }
            }

            // Entry column
            offset(ENTRY_X, COL_Y) {
                area(ENTRY_COL_W, LIST_H) { rect(0x0, 0x55) }
                widget(entryList)
                val entryCount = currentPool()?.entries?.size ?: 0
                if (entryCount > LIST_ROWS) offset(ENTRY_COL_W + 1, 0) { widget(entryListScroller) }
                offset(0, LIST_H + 4) { widget(newEntryRow) }
                offset(0, LIST_H + 4 + ROW_H + 4) { widget(deleteEntryRow) }
            }

            // Detail column
            offset(DETAIL_X, COL_Y) {
                // Sub-tab bar
                widget(basicSubTab)
                offset(SUBTAB_W + 2, 0) { widget(advSubTab) }
                offset((SUBTAB_W + 2) * 2, 0) { widget(markersSubTab) }

                if (currentEntry() == null) {
                    textShadowed(4, 28, Component.translatable("bountiful.editor.entry.none", "Select an entry"), COLOR_MUTED)
                } else {
                    when (detailTab) {
                        DetailTab.BASIC -> drawBasicTab()
                        DetailTab.ADVANCED -> drawAdvancedTab()
                        DetailTab.MARKERS -> drawMarkersTab()
                    }
                }
            }
        }

        if (newPoolModalOpen) renderNewPoolOverlay()
    }

    private fun KGuiDsl.renderNewPoolOverlay() {
        area(EditorScreen.GUI_WIDTH, EditorScreen.GUI_HEIGHT) {
            reactWith(modalBackdropReactor)
            rect(0x0, 0xB0)
        }
        val panelX = (EditorScreen.GUI_WIDTH - 240) / 2
        val panelY = (EditorScreen.GUI_HEIGHT - 80) / 2
        offset(panelX, panelY) {
            area(240, 80) { rect(0x2A1A10, 0xFF) }
            area(240, 14) { rect(0x41261b, 0xFF) }
            textCenteredColored(
                120, 3,
                Component.translatable("bountiful.editor.newpool.title", "New Pool"),
                0xFFFFE58A.toInt()
            )
        }
    }

    private val modalBackdropReactor = MouseReactor().apply {
        canPassThrough = { false }
        onClickDown = { _, _, _ -> /* swallow */ }
    }

    private fun KGuiDsl.drawBasicTab() {
        drawLabel(0, row(1), "bountiful.editor.field.type", "Type")
        offset(FIELD_X, row(1)) { widget(typeCycle) }
        drawLabel(0, row(2), "bountiful.editor.field.key", "Key")
        drawLabel(0, row(3), "bountiful.editor.field.rarity", "Rarity")
        offset(FIELD_X, row(3)) { widget(rarityCycle) }
        drawLabel(0, row(4), "bountiful.editor.field.content", "Content")
        if (!entryTypeIsCommand() && currentEntry() != null) {
            offset(FIELD_X, row(4)) { widget(contentPicker) }
        }
        drawLabel(0, row(5), "bountiful.editor.field.amount", "Amount",
            "bountiful.editor.tooltip.amount", "Amount Range")
        drawLabel(0, row(6), "bountiful.editor.field.worth", "Worth",
            "bountiful.editor.tooltip.worth", "Unit Worth")
    }

    private fun KGuiDsl.drawAdvancedTab() {
        drawLabel(0, row(1), "bountiful.editor.field.weight", "Weight x",
            "bountiful.editor.tooltip.weight", "Static Weight Multiplier")
        drawLabel(0, row(2), "bountiful.editor.field.time", "Time x",
            "bountiful.editor.tooltip.time", "Static Time Multiplier")
        drawLabel(0, row(3), "bountiful.editor.field.rep", "Rep",
            "bountiful.editor.tooltip.rep", "Reputation Requirement")
        drawLabel(0, row(4), "bountiful.editor.field.name", "Name")
        drawLabel(0, row(6), "bountiful.editor.field.biomes", "Biomes")
        offset(FIELD_X, row(6)) { widget(biomesPicker) }
        offset(FIELD_X, row(5)) {
            area(BOX_SIZE, BOX_SIZE) {
                reactWith(mysteryCheckbox.reactor)
                rect(0x1c100a, 0xF0)
                if (currentEntry()?.mystery == true) {
                    offset(2, 2) { area(BOX_SIZE - 4, BOX_SIZE - 4) { rect(0xF0C878, 0xFF) } }
                }
                onHover { rect(0xFFFFFF, 0x22) }
            }
            textShadowed(
                BOX_SIZE + 4, 1,
                Component.translatable("bountiful.editor.field.mystery", "Mystery"),
                COLOR_TEXT
            )
        }
    }

    private fun KGuiDsl.drawMarkersTab() {
        drawLabel(0, row(1), "bountiful.editor.field.markers", "Markers")
        offset(FIELD_X, row(1)) { widget(markersPicker) }
        drawLabel(0, row(2), "bountiful.editor.field.forbid_markers", "Forbids",
            "bountiful.editor.tooltip.forbid_markers", "Forbid Markers")
        offset(FIELD_X, row(2)) { widget(forbidMarkersPicker) }
    }

    private fun KGuiDsl.drawLabel(
        x: Int,
        y: Int,
        key: String,
        fallback: String,
        tooltipKey: String? = null,
        tooltipFallback: String? = null
    ) {
        val labelComp = Component.translatable(key, fallback)
        val w = fontRenderer.width(labelComp) + 2
        offset(x, y) {
            area(w, FIELD_H) {
                textShadowed(0, 3, labelComp, COLOR_TEXT)
                if (tooltipKey != null) {
                    onHover {
                        tooltip(listOf(Component.translatable(tooltipKey, tooltipFallback ?: "")))
                    }
                }
            }
        }
    }

    private fun makeEditBox(x: Int, y: Int, w: Int, hintKey: String): EditBox {
        return EditBox(parent.fontPublic, x, y, w, FIELD_H, Component.translatable(hintKey)).apply {
            setMaxLength(128)
            setCanLoseFocus(true)
        }
    }

    private fun loadEntryToForm() {
        if (!::keyBox.isInitialized) return
        val e = currentEntry()
        if (e == null) {
            for (w in allMcWidgets()) w.setValue("")
        } else {
            keyBox.setValue(e.key)
            contentBox.setValue(e.content)
            minBox.setValue(e.amountMin.toString())
            maxBox.setValue(e.amountMax.toString())
            worthBox.setValue(e.unitWorth.toString())
            weightBox.setValue(e.weightMult.toString())
            timeBox.setValue(e.timeMult.toString())
            repBox.setValue(e.repRequired.toString())
            nameBox.setValue(e.name)
        }
        applyMcWidgetVisibility()
    }

    private fun syncFormToEntry() {
        val e = currentEntry() ?: return
        if (!::keyBox.isInitialized) return
        e.key = keyBox.value.trim().ifEmpty { e.key }
        e.content = contentBox.value.trim().ifEmpty { e.content }
        e.amountMin = minBox.value.toIntOrNull() ?: e.amountMin
        e.amountMax = maxBox.value.toIntOrNull() ?: e.amountMax
        e.unitWorth = worthBox.value.toDoubleOrNull() ?: e.unitWorth
        e.weightMult = weightBox.value.toDoubleOrNull() ?: e.weightMult
        e.timeMult = timeBox.value.toDoubleOrNull() ?: e.timeMult
        e.repRequired = repBox.value.toDoubleOrNull() ?: e.repRequired
        e.name = nameBox.value
    }

    private fun updateFormEnablement() {
        if (!::keyBox.isInitialized) return
        val editable = currentEntry() != null
        for (w in allMcWidgets()) w.setEditable(editable)
        saveEntryBtn.active = editable && keyBox.value.isNotBlank()
    }

    private fun createNewEntry() {
        val pool = currentPool() ?: return
        syncFormToEntry()
        val fresh = PoolEntryDraft.empty()
        pool.entries.add(fresh)
        selectedEntryIdx = pool.entries.indexOf(fresh)
        loadEntryToForm()
    }

    private fun deleteSelectedEntry() {
        val pool = currentPool() ?: return
        val e = currentEntry() ?: return
        if (!e.isNew) DeletePoolEntryEdit(pool.id, e.originalKey).sendToServer()
        pool.entries.removeAt(selectedEntryIdx)
        selectedEntryIdx = when {
            pool.entries.isEmpty() -> -1
            selectedEntryIdx >= pool.entries.size -> pool.entries.size - 1
            else -> selectedEntryIdx
        }
        loadEntryToForm()
    }

    private fun saveSelectedEntry() {
        val pool = currentPool() ?: return
        val e = currentEntry() ?: return
        syncFormToEntry()
        val biomesJson = if (e.biomes.isEmpty()) null else {
            com.google.gson.JsonArray().apply { e.biomes.forEach { add(it) } }.toString()
        }
        val payload = PoolEntryEditorPayload(
            poolId = pool.id,
            originalPoolId = pool.originalId.takeIf { !pool.isNew },
            entryKey = e.key,
            originalEntryKey = e.originalKey.takeIf { !e.isNew },
            typeId = e.typeId,
            rarity = e.rarity.name,
            content = e.content,
            amountMin = e.amountMin,
            amountMax = e.amountMax,
            unitWorth = e.unitWorth,
            weightMult = e.weightMult,
            timeMult = e.timeMult,
            repRequired = e.repRequired,
            markersCsv = e.markers.joinToString(", "),
            forbidMarkersCsv = e.forbidMarkers.joinToString(", "),
            modifiersCsv = "",
            name = e.name,
            mystery = e.mystery,
            biomesJson = biomesJson,
            existingComponentsJson = e.existingComponentsJson,
            existingConditionsJson = e.existingConditionsJson
        )
        SavePoolEntryEdit(payload).sendToServer()
        val saved = e.copy(originalKey = e.key, isNew = false)
        pool.entries[selectedEntryIdx] = saved
        pool.entries.sortBy { it.key }
        selectedEntryIdx = pool.entries.indexOfFirst { it.key == saved.key }
        if (pool.isNew) {
            val confirmedPool = pool.copy(originalId = pool.id, isNew = false)
            pools[selectedPoolIdx] = confirmedPool
            pools.sortBy { it.id }
            selectedPoolIdx = pools.indexOfFirst { it.id == confirmedPool.id }
        }
        loadEntryToForm()
    }

    private fun deleteSelectedPool() {
        val pool = currentPool() ?: return
        if (!pool.isNew) DeletePoolEdit(pool.id).sendToServer()
        pools.removeAt(selectedPoolIdx)
        selectedPoolIdx = when {
            pools.isEmpty() -> -1
            selectedPoolIdx >= pools.size -> pools.size - 1
            else -> selectedPoolIdx
        }
        selectedEntryIdx = -1
        loadEntryToForm()
    }

    private fun openNewPoolModal() {
        newPoolModalOpen = true
        newPoolIdBox.setValue("")
        applyMcWidgetVisibility()
        newPoolIdBox.isFocused = true
    }

    private fun closeNewPoolModal() {
        newPoolModalOpen = false
        applyMcWidgetVisibility()
    }

    private fun confirmNewPool() {
        val id = newPoolIdBox.value.trim()
        if (id.isBlank() || pools.any { it.id == id }) return
        SavePoolEdit(id).sendToServer()
        val fresh = PoolDraft(id = id, originalId = id, entries = mutableListOf(), isNew = false)
        pools.add(fresh)
        pools.sortBy { it.id }
        selectedPoolIdx = pools.indexOfFirst { it.id == id }
        selectedEntryIdx = -1
        closeNewPoolModal()
        loadEntryToForm()
    }

    private fun truncated(text: String, maxPixelWidth: Int): String {
        val font = parent.fontPublic
        if (font.width(text) <= maxPixelWidth) return text
        var t = text
        while (t.isNotEmpty() && font.width("$t…") > maxPixelWidth) t = t.dropLast(1)
        return "$t…"
    }

    private fun rarityColor(r: BountyRarity): Int {
        val c = r.color.color ?: return COLOR_TEXT
        return 0xFF000000.toInt() or (c and 0xFFFFFF)
    }

    private fun row(i: Int): Int = i * (FIELD_H + FIELD_GAP)

    private inner class ActionRow(
        override val width: Int,
        private val fillColor: Int,
        private val textColor: Int,
        private val text: Component,
        private val isEnabled: () -> Boolean = { true },
        onClick: () -> Unit
    ) : KWidget {
        override val height: Int = ROW_H
        private val reactor = MouseReactor().apply {
            onClickDown = { _, _, _ -> if (isEnabled()) onClick() }
        }
        override fun onDraw(area: KGuiDsl.AreaDsl) {
            area.reactWith(reactor)
            area.dsl {
                val enabled = isEnabled()
                area(width, ROW_H) {
                    rect(if (enabled) fillColor else 0x1a1a1a, if (enabled) 0xCC else 0x88)
                    if (enabled) onHover { rect(0xFFFFFF, 0x33) }
                    val color = if (enabled) textColor else 0x80808080.toInt()
                    textShadowed(4, (ROW_H - 8) / 2, text, color)
                }
            }
        }
    }

    private inner class SubTabButton(
        override val width: Int,
        private val label: Component,
        private val isActive: () -> Boolean,
        onSelect: () -> Unit
    ) : KWidget {
        override val height: Int = SUBTAB_H
        private val reactor = MouseReactor().apply { onClickDown = { _, _, _ -> onSelect() } }
        override fun onDraw(area: KGuiDsl.AreaDsl) {
            area.reactWith(reactor)
            area.dsl {
                val active = isActive()
                area(width, SUBTAB_H) {
                    if (active) rect(0x41261b, 0xD0) else rect(0x21130b, 0xB0)
                    onHover { if (!active) rect(0xFFFFFF, 0x22) }
                }
                val textColor = if (active) 0xFFFFE58A.toInt() else 0xFFD9C0A3.toInt()
                textCenteredColored(width / 2, (SUBTAB_H - 8) / 2, label, textColor)
            }
        }
    }

    private inner class ContentPickerRow(
        override val width: Int,
        private val textProvider: () -> String,
        onClick: () -> Unit
    ) : KWidget {
        override val height: Int = FIELD_H
        private val reactor = MouseReactor().apply {
            onClickDown = { _, _, _ -> if (currentEntry() != null) onClick() }
        }
        override fun onDraw(area: KGuiDsl.AreaDsl) {
            area.reactWith(reactor)
            area.dsl {
                area(width, FIELD_H) {
                    rect(0x1c100a, 0xF0)
                    onHover { rect(0xFFFFFF, 0x1F) }
                    val text = textProvider()
                    val display = if (text.isBlank()) "<none>" else text
                    val color = if (text.isBlank()) COLOR_MUTED else COLOR_TEXT
                    // Reserve ~12px for the chevron + right padding.
                    val shown = truncated(display, width - 14)
                    textShadowed(3, (FIELD_H - 8) / 2, textLiteral(shown), color)
                    textShadowed(width - 8, (FIELD_H - 8) / 2, textLiteral("▸"), COLOR_TEXT)
                }
            }
        }
    }

    private inner class CycleButton(
        override val width: Int,
        private val options: () -> List<String>,
        private val currentValue: () -> String,
        onCycle: (String) -> Unit,
        private val colorProvider: (String) -> Int = { COLOR_TEXT }
    ) : KWidget {
        override val height: Int = FIELD_H
        private val reactor = MouseReactor().apply {
            onClickDown = { _, _, button ->
                if (currentEntry() != null) {
                    val opts = options()
                    if (opts.isNotEmpty()) {
                        val curr = currentValue()
                        val idx = opts.indexOf(curr).coerceAtLeast(0)
                        val next = if (button == 1) {
                            // right-click goes back
                            (idx - 1 + opts.size) % opts.size
                        } else {
                            (idx + 1) % opts.size
                        }
                        onCycle(opts[next])
                    }
                }
            }
        }
        override fun onDraw(area: KGuiDsl.AreaDsl) {
            area.reactWith(reactor)
            area.dsl {
                area(width, FIELD_H) {
                    rect(0x1c100a, 0xF0)
                    onHover { rect(0xFFFFFF, 0x1F) }
                    val value = currentValue()
                    textShadowed(4, (FIELD_H - 8) / 2, textLiteral(value), colorProvider(value))
                    textShadowed(width - 8, (FIELD_H - 8) / 2, textLiteral("▸"), COLOR_TEXT)
                }
            }
        }
    }

    private class CheckboxReactor(onToggle: () -> Unit) {
        val reactor: MouseReactor = MouseReactor().apply {
            onClickDown = { _, _, _ -> onToggle() }
        }
    }

    companion object {
        private const val COL_Y = 4
        private const val ROW_H = 14
        private const val LIST_ROWS = 8
        private const val LIST_H = LIST_ROWS * ROW_H
        private const val SCROLL_KNOB = 27

        private const val POOL_X = 4
        private const val POOL_COL_W = 90
        private const val ENTRY_X = POOL_X + POOL_COL_W + 10
        private const val ENTRY_COL_W = 78
        private const val DETAIL_X = ENTRY_X + ENTRY_COL_W + 8
        private const val DETAIL_Y = 4
        private const val DETAIL_W = 348 - DETAIL_X - 4

        private const val SUBTAB_W = 40
        private const val SUBTAB_H = 14

        private const val FIELD_X = 46
        private const val FIELD_W = DETAIL_W - FIELD_X - 2
        private const val CYCLE_W = FIELD_W
        private const val SMALL_W = 28
        private const val FIELD_H = 14
        private const val FIELD_GAP = 4
        private const val BOX_SIZE = 10

        private const val SAVE_Y = 136
        private const val SAVE_W = 56
        private const val SAVE_H = 16

        private val SCROLLER = Identifier.parse("container/villager/scroller")

        private const val COLOR_TEXT = 0xFFD9C0A3.toInt()
        private const val COLOR_MUTED = 0xFF8A7A63.toInt()
    }
}
