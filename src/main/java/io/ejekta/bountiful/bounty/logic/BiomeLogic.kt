package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome


object BiomeLogic : IEntryLogic {

    val biomeRegistry: Registry<Biome>?
        get() = MinecraftClient.getInstance().world?.registryManager?.get(
            Registry.BIOME_KEY
        )

    fun getBiome(entry: BountyDataEntry): Biome? {
        return biomeRegistry?.get(Identifier(entry.content))
    }

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? = null

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        return textLiteral(entry.content)
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(getDescription(entry))
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(
            if (player.world.getBiome(player.blockPos).value() == getBiome(entry)) 1 else 0,
            1
        )
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity) = true

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity) = true

}