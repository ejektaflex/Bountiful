package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.Format
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World

@Serializable
data class DecreeData(val ids: MutableList<String> = mutableListOf()) {

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
        override val identifier: Identifier = Bountiful.id("decree_data")
        override val ser = DecreeData.serializer()
        override val default = { DecreeData() }
    }

}