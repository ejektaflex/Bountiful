package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.serial.Format
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

@Serializable
data class DecreeData(val ids: MutableList<String> = mutableListOf()) {

    fun save() = Format.NBT.encodeToJsonElement(serializer(), this)

    fun tooltipInfo(world: World): List<Text> {
        val lines = mutableListOf<Text>()
        lines += when (ids.isNotEmpty()) {
            true -> {
                ids.map {
                    TranslatableText("${Bountiful.ID}.decree.$it.name").formatted(Formatting.GOLD)
                }
            }
            false -> {
                listOf(TranslatableText("bountiful.decree.notset"))
            }
        }
        return lines
    }

    companion object : ItemData<DecreeData>() {
        override val ser = DecreeData.serializer()
        override val creator: () -> DecreeData = { DecreeData() }
    }

}