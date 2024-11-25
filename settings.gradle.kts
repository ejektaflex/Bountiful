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
                val Architectury = "3.4.146" //=> https://maven.architectury.dev/architectury-plugin/architectury-plugin.gradle.plugin/
                val ArchLoom = "1.2.348" //=> https://maven.architectury.dev/dev/architectury/architectury-loom/
                val Shadow = "7.1.2"
            }

            val Platform = object {
                val MC = "1.20.1"
                // https://fabricmc.net/develop/
                val Yarn = "build.8"
                val FabricLoader = "0.14.21"
                val FabricApi = "0.84.0"
                val Forge = "47.0.19" //=> https://files.minecraftforge.net/net/minecraftforge/forge/

            }

            val Ejekta = object {
                val Kambrik = "6.1.1"
                val KambrikSnapshot = true
            }

            val Kotlin = object {
                val Version = "1.8.22"
                val Serialization = "1.5.0"
                val FabricAdapter = "1.9.5"
                val ForgeAdapter = "4.3.0"
            }

            val Mods = object {
                val Cloth = "11.0.99"
                val ModMenu = "7.1.0"
            }

            // Versions

            val ejektaGroup = "io.ejekta"
            val modPackage = "bountiful"
            val modVersion = "6.0.4"

            version("org", ejektaGroup)
            version("pkg", "$ejektaGroup.$modPackage")
            version("mod", modVersion)
            version("fullversion", "$modVersion+${Platform.MC}")
            version("yarn", "${Platform.MC}+${Platform.Yarn}")

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

            val forgeLib = "forge"
            library(forgeLib, "net.minecraftforge:forge:${Platform.MC}-${Platform.Forge}")

            // Adapters

            library("fabric-adapter", "net.fabricmc:fabric-language-kotlin:${Kotlin.FabricAdapter}+kotlin.${Kotlin.Version}")
            library("forge-adapter", "thedarkcolour:kotlinforforge:${Kotlin.ForgeAdapter}")

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
            library(modDepClothConfigForge, "me.shedaniel.cloth:cloth-config-forge:${Mods.Cloth}")

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

include("common", "fabric", "forge", "datagen")

