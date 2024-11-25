pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.minecraftforge.net/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {

    versionCatalogs {

        create("libs") {

            val Plugins = object {
                val Architectury = "3.4.155" //=> https://maven.architectury.dev/architectury-plugin/architectury-plugin.gradle.plugin/
                val ArchLoom = "1.7-SNAPSHOT" //=> https://maven.architectury.dev/dev/architectury/architectury-loom/
                val Shadow = "7.1.2"
            }

            val Platform = object {
                val MC = "1.21"
                // https://fabricmc.net/develop/
                val Yarn = "build.9"
                val FabricLoader = "0.16.0"
                val FabricApi = "0.100.4"
                val NeoForge = "21.0.86-beta" //=> https://projects.neoforged.net/neoforged/neoforge
            }

            val Ejekta = object {
                val Kambrik = "8.0.0.0011"
                val KambrikSnapshot = false
            }

            val Kotlin = object {
                val Version = "2.0.0" // => https://kotlinlang.org/docs/releases.html
                val Serialization = "1.7.0" // => https://github.com/Kotlin/kotlinx.serialization/releases
                val FabricAdapter = "1.11.0" // => https://modrinth.com/mod/fabric-language-kotlin/versions
                val ForgeAdapter = "5.3.0" //=> https://modrinth.com/mod/kotlin-for-forge/versions
            }

            val Mods = object {
                val Cloth = "15.0.127" // => https://modrinth.com/mod/cloth-config/versions
                val ModMenu = "11.0.1" // => https://modrinth.com/mod/modmenu/versions
            }

            // Versions

            val ejektaGroup = "io.ejekta"
            val modPackage = "bountiful"
            val modVersion = "8.0.0"

            version("org", ejektaGroup)
            version("pkg", "$ejektaGroup.$modPackage")
            version("mod", modVersion)
            version("fullversion", "$modVersion+${Platform.MC}")
            version("yarn", "${Platform.MC}+${Platform.Yarn}")
            version("kambrik", Ejekta.Kambrik)

            version("mc", Platform.MC)
            version("kotlin", Kotlin.Version)
            version("ksx", Kotlin.Serialization)
            version("architectury", Plugins.Architectury)
            version("archloom", Plugins.ArchLoom)
            version("shadow", Plugins.Shadow)

            // Platform dependencies

            library("minecraft", "net.minecraft:minecraft:${Platform.MC}")

            val fabricLoader = "fabric-loader"
            library(fabricLoader, "net.fabricmc:fabric-loader:${Platform.FabricLoader}")

            val neoForgeLib = "neoforge"
            library(neoForgeLib, "net.neoforged:neoforge:${Platform.NeoForge}")

            // Adapters

            library("fabric-adapter", "net.fabricmc:fabric-language-kotlin:${Kotlin.FabricAdapter}+kotlin.${Kotlin.Version}")
            library("forge-adapter", "thedarkcolour:kotlinforforge-neoforge:${Kotlin.ForgeAdapter}")

            library("fabric-api", "net.fabricmc.fabric-api:fabric-api:${Platform.FabricApi}+${Platform.MC}")

            // Self Dependencies

            val kambrikDepSuffix = "${Ejekta.Kambrik}+${Platform.MC}${if (Ejekta.KambrikSnapshot) ".SNAPSHOT+" else ""}"

            val kambrikCommon = "kambrik-common"
            library(kambrikCommon, "$ejektaGroup:$kambrikCommon:$kambrikDepSuffix")

            val kambrikFabric = "kambrik-fabric"
            library(kambrikFabric, "$ejektaGroup:$kambrikFabric:$kambrikDepSuffix")

            val kambrikForge = "kambrik-forge"
            library(kambrikForge, "$ejektaGroup:$kambrikForge:$kambrikDepSuffix")

            // Mod Dependencies

            val modDepClothConfig = "mod-dep-cloth-config"
            library(modDepClothConfig, "me.shedaniel.cloth:cloth-config:${Mods.Cloth}")

            val modDepClothConfigForge = "$modDepClothConfig-forge"
            library(modDepClothConfigForge, "me.shedaniel.cloth:cloth-config-neoforge:${Mods.Cloth}")

            val modDepModMenu = "mod-dep-mod-menu"
            library(modDepModMenu, "com.terraformersmc:modmenu:${Mods.ModMenu}")

            // Other

            library("ksx", "org.jetbrains.kotlinx:kotlinx-serialization-json:${Kotlin.Serialization}")

            // Platform bundles

            bundle("mod-deps-common", listOf(
                fabricLoader,
                kambrikCommon,
                modDepClothConfig
            ))

            bundle("mod-deps-fabric", listOf(
                fabricLoader,
                kambrikFabric,
                modDepModMenu
            ))

        }
    }
}

// TODO re-add neoforge
include("common", "fabric", "datagen")

