package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.BountyModifier
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.jvm.ensured
import io.ejekta.kambrikx.file.KambrikConfigFile
import io.ejekta.kambrikx.file.KambrikParseFailMode
import net.minecraft.SharedConstants
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*
import kotlin.math.min


object BountifulIO {

    val rootFolder = Kambrik.File.getConfigFolderRelativePath(Bountiful.ID)

    private val configFile = KambrikConfigFile(
        rootFolder,
        "bountiful.json",
        JsonFormats.Config.json,
        KambrikParseFailMode.LEAVE,
        BountifulConfigData.serializer()
    ) { BountifulConfigData() }

    private fun errFileDef(): File {
        return rootFolder.toFile().resolve("errors.log")
    }

    val errFile: File
        get() {
            return errFileDef().apply {
                if (!exists()) {
                    parentFile.mkdirs()
                    createNewFile()
                }
            }
        }

    fun emptyErrFile() {
        errFileDef().takeUnless { !it.exists() }?.writeText("")
    }

    var configData = configFile.read()

    private val poolConfigs = rootFolder.ensured("bounty_pools")
    private val decreeConfigs = rootFolder.ensured("bounty_decrees")
    private val modifierConfigs = rootFolder.ensured("bounty_modifiers")

    fun getPoolFile(poolName: String): KambrikConfigFile<Pool> {
        return KambrikConfigFile(
            poolConfigs,
            "$poolName.json", JsonFormats.Config.json, KambrikParseFailMode.LEAVE, Pool.serializer()) {
            Pool().apply { setup(poolName) }
        }
    }

    fun getDecreeFile(decreeName: String): KambrikConfigFile<Decree> {
        return KambrikConfigFile(
            decreeConfigs,
            "$decreeName.json", JsonFormats.Config.json, KambrikParseFailMode.LEAVE, Decree.serializer()
        ) {
            Decree(
                id = decreeName,
                objectives = mutableSetOf(),
                rewards = mutableSetOf()
            )
        }
    }

    fun hasDecreeConfigFile(decreeName: String): Boolean {
        return decreeConfigs.resolve("$decreeName.json").exists()
    }

    fun deleteDecreeConfigFile(decreeName: String) {
        decreeConfigs.resolve("$decreeName.json").deleteIfExists()
    }

    private fun saveConfig() {
        configFile.write(configData)
    }

    fun loadConfig() {
        configData = configFile.read()
    }

    fun reloadConfig() {
        saveConfig()
        loadConfig()
    }

    fun doContentReload(manager: ResourceManager) {
        emptyErrFile()
        reloadConfig()
        contentLoaders.forEach {
            it.clearDestination()
            it.loadData(manager)
        }
        // After all data is loaded, lint it
        contentLoaders.forEach {
            it.lint()
        }
    }

    private val contentLoaders = listOf(
        ResourceLoadStrategy("Pool Loader", "bounty_pools", poolConfigs, Pool.serializer(),
            onClear = { BountifulContent.populatePools(emptyList()) },
            onComplete = { BountifulContent.populatePools(it) },
        ) {
            // If the pool isn't being used, that's usually problematic
            if (usedInDecrees.isEmpty() && !currency) {

                val poolAssoc = BountifulContent.Pools.filter { it.usedInDecrees.isNotEmpty() }.associateBy { it.id.toSet() }
                val poolQuery = id.toSet()

                // Compare to existing pool names based on char similarity to see if they just misspelled it
                val bestFit = poolAssoc.maxByOrNull { it.key.intersect(poolQuery).size }?.takeIf {
                    // Must be at most two characters off
                    it.key.intersect(poolQuery).size >= min(poolQuery.size, it.key.size) - 2
                }

                val warnings = mutableListOf(
                    "Pool '$id' has been loaded, but is not attached to any existing data! This is probably a configuration error.",
                    "If you intended to add this data to an existing Pool, please use an existing Pool name instead of '$id'."
                )

                bestFit?.let {
                    warnings.add("Did you mean to use the pool name '${it.value.id}' instead of '$id'?")
                }

                warnings.add("Otherwise, please add '$id' to a Decree.")
                warnings.add("NOTE: This data will not show up in game until one of the above fixes is made.")

                Bountiful.logAndWarn(warnings)
            }
        },
        ResourceLoadStrategy("Modifier Loader", "bounty_modifiers", modifierConfigs, BountyModifier.serializer(),
            onClear = { BountifulContent.populateModifiers(emptyList()) },
            onComplete = { BountifulContent.populateModifiers(it) }
        ) {
            if (type == BountyModifier.Type.ENCHANTS && levels.min <= 0) {
                Bountiful.logAndWarn("Modifier '$id' defines a non-positive minimum enchant level (${levels.min}).")
            }
        },
        ResourceLoadStrategy("Decree Loader", "bounty_decrees", decreeConfigs, Decree.serializer(),
            onClear = { BountifulContent.Decrees.clear() },
            onComplete = { BountifulContent.Decrees.addAll(it) }
        ) {
            if (objectivePools.isEmpty()) {
                Bountiful.logAndWarn("Decree '$id' has no Objective Pools! This is probably a configuration error.")
            } else if (objectives.isEmpty()) {
                Bountiful.logAndWarn("Decree '$id' has one or more Objective Pools, but they are all empty!")
            }

            if (rewardPools.isEmpty()) {
                Bountiful.logAndWarn("Decree '$id' has no Reward Pools! This is probably a configuration error.")
            } else if (rewards.isEmpty()) {
                Bountiful.logAndWarn("Decree '$id' has one or more Reward Pools, but they are all empty!")
            }

            invalidPools.let {
                if (it.isNotEmpty()) {
                    Bountiful.logAndWarn("Decree '$id' references these pools, which do not exist: $it")
                }
            }

            val topRewards = allRewardEntries.run {
                sortedBy { -it.maxWorth }.take(configData.bounty.initialCountPreference.max.coerceAtMost(size))
            }

            val topObjectives = allObjectiveEntries.run {
                sortedBy { -it.maxWorth }.take(configData.bounty.fillerCountPreference.min.coerceAtLeast(1))
            }

            val rewWorstWorth = topRewards.sumOf { it.maxWorth }
            val objWorstWorth = topObjectives.sumOf { it.maxWorth }

            // If the top X number of objs can't meet the worth of the top X rewards, warn the user
            if (objWorstWorth < rewWorstWorth * 0.9) {
                Bountiful.logAndWarn(
                    "Decree '$id' top value rewards cannot be matched with equivalent objectives. This will result in uneven bounties. Consider adding more high value objectives or lowering the value of your rewards.",
                    "Top Rewards: ${topRewards.joinToString(", ") { "${it.id} (${it.maxWorth})" }} total up to: $rewWorstWorth",
                    "Top Objs: ${topObjectives.joinToString(", ") { "${it.id} (${it.maxWorth})" }} total up to: $objWorstWorth"
                )
            }

        }
    )


    @OptIn(ExperimentalPathApi::class)
    fun exportDataPack(named: String, description: String) {
        val tmpSpot = rootFolder.resolve("tmp_pack")

        val dataSpot = tmpSpot.resolve("data").resolve(Bountiful.ID)

        // Folder copying

        val poolSpot = dataSpot.resolve(poolConfigs.fileName).resolve(Bountiful.ID)

        poolConfigs.copyToRecursively(poolSpot.createParentDirectories(), { src, target, e ->
            OnErrorResult.TERMINATE
        }, followLinks = false)

        val decreeSpot = dataSpot.resolve(decreeConfigs.fileName).resolve(Bountiful.ID).apply {
            toFile().mkdirs()
        }

        decreeConfigs.copyToRecursively(decreeSpot.createParentDirectories(), { src, target, e ->
            e.printStackTrace()
            OnErrorResult.TERMINATE
        }, followLinks = false)

        val modifierSpot = dataSpot.resolve(modifierConfigs.fileName).resolve(Bountiful.ID).apply {
            toFile().mkdirs()
        }

        modifierConfigs.copyToRecursively(modifierSpot.createParentDirectories(), { src, target, e ->
            e.printStackTrace()
            OnErrorResult.TERMINATE
        }, followLinks = false)

        // Pack Icon copying

        val bbImg = Bountybridge.getClassLoader().getResourceAsStream("assets/bountiful/textures/block/bountyboard.png")

        val data = bbImg?.readAllBytes()

        println("Data: $data")

        data?.let {
            tmpSpot.resolve("pack.png").apply {
                deleteIfExists()
                writeBytes(it)
            }
        }

        // mcmeta creation

        val resVersion = SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).major()

        tmpSpot.resolve("pack.mcmeta").writeLines(listOf(
            "{",
            "\t\"pack\": {",
            "\t\t\"pack_format\": $resVersion,",
            "\t\t\"description\": \"$description\"",
            "\t}",
            "}"
        ))

        // Zip name sanitation

        val zipName = named
            .replace(Regex("\\W+"), "")

        if (zipName.isBlank()) {
            throw Exception("Zip name (after removing non-file characters) is blank!")
        }

        // Final zip

        zipToPath(
            rootFolder.resolve("exportedPacks").createParentDirectories().resolve("$zipName.zip"),
            tmpSpot
        )

        // Delete tmp data

        tmpSpot.let {
            if (it.exists()) {
                it.deleteRecursively()
            }
        }

    }

    @OptIn(ExperimentalPathApi::class)
    private fun zipToPath(destPath: Path, srcPath: Path) {
        ZipOutputStream(Files.newOutputStream(destPath)).use { zs ->
            for (item in srcPath.walk(PathWalkOption.BREADTH_FIRST)) {
                if (!item.isDirectory()) {
                    val ze = ZipEntry(srcPath.relativize(item).toString())
                    try {
                        zs.putNextEntry(ze)
                        Files.copy(item, zs)
                        zs.closeEntry()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}
