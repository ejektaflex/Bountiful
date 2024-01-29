package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.client.widgets.BountyLongButton
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.widgets.KListWidget
import io.ejekta.kambrik.gui.draw.widgets.KScrollbarVertical
import io.ejekta.kambrik.gui.screen.KambrikHandledScreen
import io.ejekta.kambrik.gui.screen.KambrikScreen
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class AnalyzerScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) : KambrikHandledScreen<ScreenHandler>(
    handler, inventory, title
) {

    init {
        backgroundWidth = 348
        backgroundHeight = 165
    }

    private val bgGui = kambrikGui {
        //img(TEXTURE, 349, 166)
    }


//    private val validButtons: List<BountyLongButton>
//        get() = buttons.filter { it.getBountyData().objectives.isNotEmpty() }

    //private val scroller = KScrollbarVertical(140, 6, 27, SCROLLER, 0x0)

//    private val buttonList = KListWidget(
//        { validButtons }, 160, 20, 7, KListWidget.Orientation.VERTICAL, KListWidget.Mode.SINGLE,
//        { listWidget, item, selected ->
//            widget(item)
//        }
//    ).apply {
//        reactor.canPassThrough = { true }
//        attachScrollbar(scroller)
//    }

    private fun drawGui(): KGui {
        return kambrikGui {

            area(backgroundWidth, backgroundHeight) {
                rect(0x888888)
                text(0, 0) {
                    addLiteral("Hello There!")
                }
            }
        }
    }

    private val fgGui = drawGui()

    override fun onDrawBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // do nothing
    }

    override fun onDrawForeground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        bgGui.draw(context, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
    }

    companion object {
//        private val TEXTURE = Bountiful.id("board_bg")
//        private val SELECTOR = Bountiful.id("selector")
//        private val SCROLLER = Identifier("container/villager/scroller")
//        private val XP_FG = Identifier("container/villager/experience_bar_current")
//        private val XP_BG = Identifier("container/villager/experience_bar_background")
    }
}

