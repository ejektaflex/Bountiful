package io.ejekta.bountiful.client.widgets

import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KWidget
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.awt.Color

class AnalyzerPoolWidget(val pool: Pool, maxWorth: Double, val pixSize: Int, val inHeight: Int) : KWidget {
    override val height: Int
        get() = inHeight

    override val width: Int
        get() = 140

    val leftBarWidth = 20

    val numPixels = (width - leftBarWidth) / pixSize

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

    val poolHue = (pool.id.sumOf { it.code } % 255) / 255f

    val poolBlockColor = Color.getHSBColor(poolHue, 0.6f, 0.6f).rgb

    override fun onDraw(area: KGuiDsl.AreaDsl) {

        area.dsl {

            area(leftBarWidth, height) {
                rect(poolBlockColor)
                onHover {
                    tooltip {
                        addLiteral("Pool: ${pool.id}")
                    }
                }
            }

            area(leftBarWidth, 0,width - leftBarWidth, height) {

                for (i in 0 until numPixels) {
                    //rect(i * 2, 0, 2, 2, 0x88 * (stepMap[i] ?: emptySet()).size)
                    rect(i * pixSize, 0, pixSize, height, stepColors[i] ?: 0x0)
                }

                val currX = area.dsl.ctx.absX()

                onHover {
                    val pixBin = (mouseX - currX) / pixSize
                    val res = stepMap[pixBin] ?: emptySet()

                    val texts = mutableListOf<MutableComponent>()

                    val worthLow = pixBin * binWidth
                    val worthHigh = (pixBin + 1) * binWidth

                    texts.add(
                        Component.literal("Entries at worth range ${worthLow.toInt()}-${worthHigh.toInt()}: ${res.size}")
                            .withStyle(ChatFormatting.GOLD)
                    )

                    for (item in res) {
                        texts.add(
                            Component.literal(item.id.substringAfter("${pool.id}.")).withStyle(ChatFormatting.GREEN)
                                .append(
                                    Component.literal(" (${item.content})").withStyle(item.rarity.color)
                                )
                        )
                    }

                    tooltip(texts)
                }
            }
            area(width, height) {

            }
        }
    }

}