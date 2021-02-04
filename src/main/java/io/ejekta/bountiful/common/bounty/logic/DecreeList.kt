package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.serial.Format
import io.ejekta.bountiful.common.util.GameTime
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.max

@Serializable
data class DecreeList(val ids: MutableList<String> = mutableListOf()) {

    fun save() = Format.NBT.encodeToJsonElement(serializer(), this)

    fun tooltipInfo(world: World): List<Text> {
        val lines = mutableListOf<Text>()
        lines += ids.map {
            TranslatableText("${Bountiful.ID}.decree.$it.name").formatted(Formatting.GOLD)
        }
        return lines
    }

    companion object : ItemData<DecreeList>(serializer()) {

        override val creator: () -> DecreeList = { DecreeList() }

    }

}