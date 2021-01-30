package io.ejekta.bountiful.common.bounty.logic

import net.minecraft.util.Formatting

enum class BountyRarity(val color: Formatting) {
    COMMON(Formatting.WHITE),
    UNCOMMON(Formatting.AQUA),
    RARE(Formatting.YELLOW),
    EPIC(Formatting.LIGHT_PURPLE),
    LEGENDARY(Formatting.GOLD)
}