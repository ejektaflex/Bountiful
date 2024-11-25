package io.ejekta.bountiful.chaos

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO.rootFolder
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.kambrikx.file.KambrikConfigFile
import io.ejekta.kambrikx.file.KambrikParseFailMode
import net.minecraft.server.MinecraftServer

class ChaosMode {

    private val chaosFile = KambrikConfigFile(
        rootFolder,
        "bountiful-chaos.json",
        JsonFormats.Config.json,
        KambrikParseFailMode.LEAVE,
        BountifulChaosData.serializer()
    ) { BountifulChaosData() }

    private val chaosFileInfo = KambrikConfigFile(
        rootFolder,
        "bountiful-chaos-info.json",
        JsonFormats.Config.json,
        KambrikParseFailMode.LEAVE,
        BountifulChaosInfo.serializer()
    ) { BountifulChaosInfo() }

    private var chaosData = chaosFile.read()
    private var chaosInfo = chaosFileInfo.read()

    fun inject(server: MinecraftServer) {
        Bountiful.LOGGER.info("Injecting chaos into Bountiful...")
        chaosData = chaosFile.read()
        chaosInfo = chaosFileInfo.read()

        val solver = DepthSolver(server, chaosData, chaosInfo)
        solver.solveRequiredRecipes()

        Bountiful.LOGGER.info("Done with solves!")

        solver.syncConfig()

        chaosFile.write(chaosData)
        chaosFileInfo.write(chaosInfo)

        solver.sendToRegistries()

        Bountiful.LOGGER.info("Chaos sent!")
    }
}