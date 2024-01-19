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
                val Architectury = "3.4.151" //=> https://maven.architectury.dev/architectury-plugin/architectury-plugin.gradle.plugin/
                val ArchLoom = "1.4-SNAPSHOT" //=> https://maven.architectury.dev/dev/architectury/architectury-loom/
                val Shadow = "7.1.2"
            }

            val Platform = object {
                val MC = "1.20.4"
                // https://fabricmc.net/develop/
                val Yarn = "build.3"
                val FabricLoader = "0.15.3"
                val FabricApi = "0.93.1"
                val NeoForge = "20.4.114-beta" //=> https://projects.neoforged.net/neoforged/neoforge

            }

            val Ejekta = object {
                val Kambrik = "7.0.0"
                val KambrikSnapshot = true
            }

            val Kotlin = object {
                val Version = "1.9.22" // => https://kotlinlang.org/docs/releases.html
                val Serialization = "1.6.0" // => https://github.com/Kotlin/kotlinx.serialization/releases
                val FabricAdapter = "1.10.17" // => https://modrinth.com/mod/fabric-language-kotlin/versions
                val ForgeAdapter = "4.10.0" // => https://modrinth.com/mod/kotlin-for-forge/versions
            }

            val Mods = object {
                val Cloth = "13.0.121" // => https://modrinth.com/mod/cloth-config/versions
                val ModMenu = "9.0.0" // => https://modrinth.com/mod/modmenu/versions
            }

            // Versions

            val ejektaGroup = "io.ejekta"
            val modPackage = "bountiful"
            val modVersion = "7.0.0"

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

            val kambrikDepSuffix = "${Ejekta.Kambrik}+${Platform.MC}${if (Ejekta.KambrikSnapshot) ".SNAPSHOT+" else null}"

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

include("common", "fabric", "neoforge", "datagen")

