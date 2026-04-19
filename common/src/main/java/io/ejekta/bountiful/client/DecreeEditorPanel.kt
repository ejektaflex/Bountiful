package io.ejekta.bountiful.client

import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.editor.DecreeEditorPayload
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.messages.DeleteDecreeEdit
import io.ejekta.bountiful.messages.SaveDecreeEdit
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
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

internal data class DecreeDraft(
    var id: String,
    val originalId: String,
    var name: String,
    val objectives: MutableList<String>,
    val rewards: MutableList<String>,
    val professions: MutableList<String>,
    var canSpawn: Boolean,
    var canReveal: Boolean,
    var canWanderBuy: Boolean,
    val isNew: Boolean = false
) {
    companion object {
        fun from(d: Decree): DecreeDraft = DecreeDraft(
            id = d.id,
            originalId = d.id,
            name = d.name.orEmpty(),
            objectives = d.objectives.sorted().toMutableList(),
            rewards = d.rewards.sorted().toMutableList(),
            professions = d.linkedProfessions.sorted().toMutableList(),
            canSpawn = d.canSpawn,
            canReveal = d.canReveal,
            canWanderBuy = d.canWanderBuy
        )

        fun empty(): DecreeDraft = DecreeDraft(
            id = "new_decree",
            originalId = "new_decree",
            name = "",
            objectives = mutableListOf(),
            rewards = mutableListOf(),
            professions = mutableListOf(),
            canSpawn = true,
            canReveal = true,
            canWanderBuy = true,
            isNew = true
        )
    }
}

class DecreeEditorPanel(private val parent: EditorScreen) {

    enum class PickerKind { OBJECTIVES, REWARDS, PROFESSIONS }

    private val drafts: MutableList<DecreeDraft> = BountifulContent.Decrees
        .sortedBy { it.id }
        .map { DecreeDraft.from(it) }
        .toMutableList()

    private var selectedIndex: Int = if (drafts.isNotEmpty()) 0 else -1

    private lateinit var idBox: EditBox
    private lateinit var nameBox: EditBox
    private lateinit var saveBtn: Button

    private var panelVisible: Boolean = true
    private var activeModal: EditorPickerModal? = null

    private val listScroller = KScrollbarVertical(LIST_ROWS * ROW_H, 6, SCROLL_KNOB_H, SCROLLER, 0x0)

    private val decreeList = KListWidget(
        items = { drafts },
        itemWidth = LIST_W,
        itemHeight = ROW_H,
        shown = LIST_ROWS,
        orientation = KListWidget.Orientation.VERTICAL,
        mode = KListWidget.Mode.SINGLE,
        onDrawItemFunc = { _, draft, _ ->
            val idx = drafts.indexOf(draft)
            val isSelected = idx == selectedIndex
            area(LIST_W, ROW_H) {
                if (isSelected) rect(0x41261b, 0xCC) else rect(0x1c100a, 0x88)
                onHover { if (!isSelected) rect(0xFFFFFF, 0x22) }
                val label = draft.id.ifBlank { "<unnamed>" } + if (draft.isNew) " *" else ""
                val color = if (isSelected) 0xFFFFE58A.toInt() else 0xFFD9C0A3.toInt()
                textShadowed(4, (ROW_H - 8) / 2, textLiteral(label), color)
            }
        }
    ).apply {
        attachScrollbar(listScroller)
        reactor.canPassThrough = { true }
        reactor.onClickDown = { _, ry, _ ->
            val row = ry / ROW_H
            val rangeList = shownRange.toList()
            val targetIndex = rangeList.getOrNull(row)
            if (targetIndex != null && targetIndex in drafts.indices) {
                syncFormToDraft()
                selectedIndex = targetIndex
                loadDraftToForm()
            }
        }
    }

    private val newRow = ActionRow(
        width = LIST_W,
        fillColor = 0x1f3a1c,
        textColor = 0xFFD7F0B9.toInt(),
        text = Component.translatable("bountiful.editor.decree.new", "+ New Decree"),
        onClick = { createNew() }
    )

    private val deleteRow = ActionRow(
        width = LIST_W,
        fillColor = 0x5a1a1a,
        textColor = 0xFFF0B9B9.toInt(),
        text = Component.translatable("bountiful.editor.decree.delete", "- Delete Decree"),
        isEnabled = { currentDraft() != null && currentDraft()?.isNew != true },
        onClick = { deleteSelected() }
    )

    private val objectivesPicker = PickerRow(
        width = PICKER_W,
        labelKey = "bountiful.editor.field.objectives",
        fallback = "Obj Pools",
        countProvider = { currentDraft()?.objectives?.size ?: 0 },
        warnOnEmpty = true,
        onClick = { openPicker(PickerKind.OBJECTIVES) }
    )
    private val rewardsPicker = PickerRow(
        width = PICKER_W,
        labelKey = "bountiful.editor.field.rewards",
        fallback = "Rew Pools",
        countProvider = { currentDraft()?.rewards?.size ?: 0 },
        warnOnEmpty = true,
        onClick = { openPicker(PickerKind.REWARDS) }
    )
    private val professionsPicker = PickerRow(
        width = PICKER_W,
        labelKey = "bountiful.editor.field.professions",
        fallback = "Professions",
        countProvider = { currentDraft()?.professions?.size ?: 0 },
        onClick = { openPicker(PickerKind.PROFESSIONS) }
    )

    private val checkboxSpawn = CheckboxReactorBundle { currentDraft()?.let { it.canSpawn = !it.canSpawn } }
    private val checkboxReveal = CheckboxReactorBundle { currentDraft()?.let { it.canReveal = !it.canReveal } }
    private val checkboxWander = CheckboxReactorBundle { currentDraft()?.let { it.canWanderBuy = !it.canWanderBuy } }

    fun build() {
        val bx = parent.bodyX()
        val by = parent.bodyY()
        val formX = bx + FORM_X
        val formY = by + FORM_Y

        idBox = makeEditBox(formX + FIELD_X, formY + row(0), FIELD_W, "bountiful.editor.field.id")
        nameBox = makeEditBox(formX + FIELD_X, formY + row(1), FIELD_W, "bountiful.editor.field.name")

        parent.addPanelWidget(idBox)
        parent.addPanelWidget(nameBox)

        saveBtn = Button.builder(
            Component.translatable("bountiful.editor.btn.save", "Save")
        ) { saveSelected() }
            .bounds(formX + (FORM_W - SAVE_BTN_W), formY + SAVE_BTN_Y, SAVE_BTN_W, SAVE_BTN_H)
            .build()
        parent.addPanelWidget(saveBtn)

        loadDraftToForm()
    }

    fun setVisible(visible: Boolean) {
        panelVisible = visible
        if (!::idBox.isInitialized) return
        applyMcWidgetVisibility()
    }

    private fun applyMcWidgetVisibility() {
        val showForm = panelVisible && activeModal == null
        idBox.visible = showForm
        nameBox.visible = showForm
        saveBtn.visible = showForm
        if (!showForm) {
            idBox.isFocused = false
            nameBox.isFocused = false
        }
        updateFormEnablement()
    }

    fun drawInto(dsl: KGuiDsl) {
        val modal = activeModal
        if (modal != null) {
            modal.drawInto(dsl)
            return
        }
        with(dsl) { renderPanelBody() }
    }

    private fun KGuiDsl.renderPanelBody() {
        offset(0, EditorScreen.TAB_STRIP_HEIGHT) {
            // Left column
            offset(LIST_X, LIST_Y) {
                area(LIST_W, LIST_ROWS * ROW_H) { rect(0x0, 0x55) }
                widget(decreeList)
                if (drafts.size > LIST_ROWS) {
                    offset(LIST_W + 2, 0) { widget(listScroller) }
                }
                offset(0, LIST_ROWS * ROW_H + 4) { widget(newRow) }
                offset(0, LIST_ROWS * ROW_H + 4 + ROW_H + 4) { widget(deleteRow) }
            }

            // Right form
            offset(FORM_X, FORM_Y) {
                drawLabel(0, row(0), "bountiful.editor.field.id", "ID")
                drawLabel(0, row(1), "bountiful.editor.field.name", "Name")

                offset(FIELD_X, row(2)) { widget(objectivesPicker) }
                offset(FIELD_X, row(3)) { widget(rewardsPicker) }
                offset(FIELD_X, row(4)) { widget(professionsPicker) }

                drawLabel(0, row(2), "bountiful.editor.field.objectives", "Obj Pools")
                drawLabel(0, row(3), "bountiful.editor.field.rewards", "Rew Pools")
                drawLabel(0, row(4), "bountiful.editor.field.professions", "Professions")

                offset(0, CHECKBOX_ROW_Y) {
                    offset(CHECK_X_SPAWN, 0) {
                        drawCheckbox("bountiful.editor.field.can_spawn", "Spawn", checkboxSpawn) {
                            currentDraft()?.canSpawn == true
                        }
                    }
                    offset(CHECK_X_REVEAL, 0) {
                        drawCheckbox("bountiful.editor.field.can_reveal", "Reveal", checkboxReveal) {
                            currentDraft()?.canReveal == true
                        }
                    }
                    offset(CHECK_X_WANDER, 0) {
                        drawCheckbox("bountiful.editor.field.can_wander_buy", "Wander", checkboxWander) {
                            currentDraft()?.canWanderBuy == true
                        }
                    }
                }
            }
        }
    }

    private fun KGuiDsl.drawLabel(x: Int, y: Int, key: String, fallback: String) {
        textShadowed(x, y + 3, Component.translatable(key, fallback), 0xFFD9C0A3.toInt())
    }

    private fun KGuiDsl.drawCheckbox(
        key: String,
        fallback: String,
        bundle: CheckboxReactorBundle,
        isChecked: () -> Boolean
    ) {
        area(BOX_SIZE, BOX_SIZE) {
            reactWith(bundle.reactor)
            rect(0x1c100a, 0xF0)
            if (isChecked()) {
                offset(2, 2) { area(BOX_SIZE - 4, BOX_SIZE - 4) { rect(0xF0C878, 0xFF) } }
            }
            onHover { rect(0xFFFFFF, 0x22) }
        }
        textShadowed(BOX_SIZE + 3, 1, Component.translatable(key, fallback), 0xFFD9C0A3.toInt())
    }

    private class CheckboxReactorBundle(onToggle: () -> Unit) {
        val reactor: MouseReactor = MouseReactor().apply {
            onClickDown = { _, _, _ -> onToggle() }
        }
    }

    private fun makeEditBox(x: Int, y: Int, w: Int, hintKey: String): EditBox {
        return EditBox(parent.fontPublic, x, y, w, FIELD_H, Component.translatable(hintKey)).apply {
            setMaxLength(128)
            setCanLoseFocus(true)
        }
    }

    private fun currentDraft(): DecreeDraft? = drafts.getOrNull(selectedIndex)

    private fun loadDraftToForm() {
        val d = currentDraft()
        if (d == null) {
            if (::idBox.isInitialized) {
                idBox.setValue("")
                nameBox.setValue("")
            }
        } else {
            if (::idBox.isInitialized) {
                idBox.setValue(d.id)
                nameBox.setValue(d.name)
            }
        }
        updateFormEnablement()
    }

    private fun updateFormEnablement() {
        if (!::idBox.isInitialized) return
        val hasSelection = currentDraft() != null
        idBox.setEditable(hasSelection)
        nameBox.setEditable(hasSelection)
        saveBtn.active = hasSelection && idBox.value.isNotBlank()
    }

    private fun syncFormToDraft() {
        val d = currentDraft() ?: return
        if (!::idBox.isInitialized) return
        d.id = idBox.value.trim()
        d.name = nameBox.value
    }

    private fun createNew() {
        syncFormToDraft()
        val fresh = DecreeDraft.empty()
        drafts.add(fresh)
        selectedIndex = drafts.indexOf(fresh)
        loadDraftToForm()
    }

    private fun saveSelected() {
        val d = currentDraft() ?: return
        syncFormToDraft()
        val newId = d.id.trim()
        if (newId.isBlank()) return
        val payload = DecreeEditorPayload(
            id = newId,
            originalId = d.originalId.takeIf { !d.isNew },
            name = d.name,
            objectivesCsv = d.objectives.joinToString(", "),
            rewardsCsv = d.rewards.joinToString(", "),
            linkedProfessionsCsv = d.professions.joinToString(", "),
            canSpawn = d.canSpawn,
            canReveal = d.canReveal,
            canWanderBuy = d.canWanderBuy
        )
        SaveDecreeEdit(payload).sendToServer()
        val saved = DecreeDraft(
            id = newId,
            originalId = newId,
            name = d.name,
            objectives = d.objectives.toMutableList(),
            rewards = d.rewards.toMutableList(),
            professions = d.professions.toMutableList(),
            canSpawn = d.canSpawn,
            canReveal = d.canReveal,
            canWanderBuy = d.canWanderBuy,
            isNew = false
        )
        drafts[selectedIndex] = saved
        drafts.sortBy { it.id }
        selectedIndex = drafts.indexOfFirst { it.id == newId }
        loadDraftToForm()
    }

    private fun deleteSelected() {
        val d = currentDraft() ?: return
        if (d.isNew) {
            // never persisted — just drop locally
        } else {
            DeleteDecreeEdit(d.originalId).sendToServer()
        }
        drafts.removeAt(selectedIndex)
        selectedIndex = when {
            drafts.isEmpty() -> -1
            selectedIndex >= drafts.size -> drafts.size - 1
            else -> selectedIndex
        }
        loadDraftToForm()
    }

    private fun openPicker(kind: PickerKind) {
        val d = currentDraft() ?: return
        syncFormToDraft()
        val title: Component
        val options: List<String>
        val current: MutableList<String>
        when (kind) {
            PickerKind.OBJECTIVES -> {
                title = Component.translatable("bountiful.editor.picker.title.objectives", "Objective Pools")
                options = BountifulContent.Pools.map { it.id }
                current = d.objectives
            }
            PickerKind.REWARDS -> {
                title = Component.translatable("bountiful.editor.picker.title.rewards", "Reward Pools")
                options = BountifulContent.Pools.map { it.id }
                current = d.rewards
            }
            PickerKind.PROFESSIONS -> {
                title = Component.translatable("bountiful.editor.picker.title.professions", "Linked Professions")
                options = BuiltInRegistries.VILLAGER_PROFESSION.keySet().map { it.toString() }
                current = d.professions
            }
        }
        activeModal = EditorPickerModal(parent, title, options, current) { newSelection ->
            current.clear()
            current.addAll(newSelection)
            activeModal = null
            applyMcWidgetVisibility()
        }
        applyMcWidgetVisibility()
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

    private inner class PickerRow(
        override val width: Int,
        private val labelKey: String,
        private val fallback: String,
        private val countProvider: () -> Int,
        private val warnOnEmpty: Boolean = false,
        onClick: () -> Unit
    ) : KWidget {
        override val height: Int = FIELD_H
        private val reactor = MouseReactor().apply {
            onClickDown = { _, _, _ -> if (currentDraft() != null) onClick() }
        }
        override fun onDraw(area: KGuiDsl.AreaDsl) {
            area.reactWith(reactor)
            area.dsl {
                area(width, FIELD_H) {
                    rect(0x1c100a, 0xF0)
                    onHover { rect(0xFFFFFF, 0x1F) }
                    val n = countProvider()
                    val textColor = if (warnOnEmpty && n == 0) COLOR_WARN else COLOR_TEXT
                    textShadowed(3, (FIELD_H - 8) / 2, textLiteral("$n selected"), textColor)
                    textShadowed(width - 8, (FIELD_H - 8) / 2, textLiteral("▸"), COLOR_TEXT)
                }
            }
        }
    }

    companion object {
        private const val LIST_X = 6
        private const val LIST_Y = 6
        private const val LIST_W = 116
        private const val LIST_ROWS = 8
        private const val ROW_H = 14
        private const val SCROLL_KNOB_H = 27

        private const val FORM_X = 138
        private const val FORM_Y = 6
        private const val FORM_W = 204
        private const val FIELD_X = 60
        private const val FIELD_W = 140
        private const val PICKER_W = 140
        private const val FIELD_H = 14
        private const val FIELD_GAP = 6

        private const val CHECKBOX_ROW_Y = 108
        private const val BOX_SIZE = 10
        private const val CHECK_X_SPAWN = 0
        private const val CHECK_X_REVEAL = 60
        private const val CHECK_X_WANDER = 120

        private const val SAVE_BTN_Y = 132
        private const val SAVE_BTN_W = 60
        private const val SAVE_BTN_H = 16

        private val SCROLLER = Identifier.parse("container/villager/scroller")

        private const val COLOR_TEXT = 0xFFD9C0A3.toInt()
        private const val COLOR_WARN = 0xFFFF8080.toInt()
    }
}
