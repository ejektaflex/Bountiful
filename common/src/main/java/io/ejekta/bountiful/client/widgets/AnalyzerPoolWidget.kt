package io.ejekta.bountiful.client.widgets

import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.awt.Color

class AnalyzerPoolWidget(pool: Pool, maxWorth: Double) : KWidget {

    val pixSize = 10 // width and pixSize must divide evenly, probably
    override val height: Int
        get() = pixSize

    override val width: Int
        get() = 120

    val numPixels = width / pixSize

    val binWidth = maxWorth / numPixels

    val stepMap = mutableMapOf<Int, MutableSet<PoolEntry>>()

    init {
        for (pe in pool) {
            for (step in pe.worthSteps) {
                val binNum = (step / binWidth).toInt()
                val bin = stepMap.getOrPut(binNum) { mutableSetOf() }
                bin.add(pe)
            }
        }
    }

    val stepMax = stepMap.maxOf { it.value.size }

    val stepColors = stepMap.map {
        // 1.0 -> green (0.333), 0.0 -> red (0.0)
        val amt = it.value.size.toDouble() / stepMax
        it.key to Color.getHSBColor(amt.toFloat() * 0.333f, 1f, 1f).rgb
    }.toMap()

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        val currX = area.dsl.ctx.absX()
        area.dsl {
            for (i in 0 until numPixels) {
                //rect(i * 2, 0, 2, 2, 0x88 * (stepMap[i] ?: emptySet()).size)
                rect(i * pixSize, 0, pixSize, pixSize, stepColors[i] ?: 0x0)
            }
            area(width, height) {
                onHover {
                    val pixBin = (mouseX - currX) / pixSize
                    val res = stepMap[pixBin] ?: emptySet()

                    val texts = mutableListOf<MutableText>()

                    val worthLow = pixBin * binWidth
                    val worthHigh = (pixBin + 1) * binWidth

                    texts.add(
                        Text.literal("Entries at worth range ${worthLow.toInt()}-${worthHigh.toInt()}: ${res.size}")
                            .formatted(Formatting.GOLD)
                    )

                    for (item in res) {
                        texts.add(
                            Text.literal(item.id).formatted(Formatting.GREEN)
                                .append(
                                    Text.literal(" (${item.content})").formatted(item.rarity.color)
                                )
                        )
                    }

                    tooltip(texts)
                }
            }
        }
    }

}