package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Serializable
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World

@Suppress("RemoveRedundantQualifierName")
@Serializable
data class DecreeData(val ids: MutableList<String> = mutableListOf()) {

    fun tooltipInfo(world: World): List<Text> {
        return mutableListOf<Text>() + when (ids.isNotEmpty()) {
            true -> {
                ids.map {
                    val dec = BountifulContent.Decrees.firstOrNull { d -> d.id == it }
                    val toText = if (dec?.name != null) {
                        Text.literal(dec.name)
                    } else {
                        Text.translatable("${Bountiful.ID}.decree.$it.name")
                    }
                    toText.formatted(Formatting.GOLD)
                }
            }
            false -> {
                listOf(Text.translatable("bountiful.decree.notset"))
            }
        }
    }


    companion object : ItemDataJson<DecreeData>() {
        override val identifier: Identifier = Bountiful.id("decree_data")
        override val ser = DecreeData.serializer()
        override val default = { DecreeData() }
    }

}