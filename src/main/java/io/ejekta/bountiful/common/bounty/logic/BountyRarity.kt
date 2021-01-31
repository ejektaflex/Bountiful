package io.ejekta.bountiful.common.bounty.logic

import net.minecraft.util.Formatting

enum class BountyRarity(val color: Formatting, val weight: Int) {
    COMMON(Formatting.WHITE, 1024),
    UNCOMMON(Formatting.AQUA, 512),
    RARE(Formatting.YELLOW, 256),
    EPIC(Formatting.LIGHT_PURPLE, 128),
    LEGENDARY(Formatting.GOLD, 64)
}