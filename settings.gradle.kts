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
                val ArchLoom = "1.4-SNAPSHOT" //=> https://maven.architectury.dev/dev/architectury/architectury-loom/
                val Shadow = "7.1.2"
            }

            val Platform = object {
                val MC = "1.20.2"
                // https://fabricmc.net/develop/
                val Yarn = "build.4"
                val FabricLoader = "0.14.23"
                val FabricApi = "0.90.4"
                val Forge = "48.0.31" //=> https://files.minecraftforge.net/net/minecraftforge/forge/

            }

            val Ejekta = object {
                val Kambrik = "6.2.0"
                val KambrikSnapshot = true
            }

            val Kotlin = object {
                val Version = "1.9.10" // => https://kotlinlang.org/docs/releases.html
                val Serialization = "1.6.0" // => https://github.com/Kotlin/kotlinx.serialization/releases
                val FabricAdapter = "1.10.10" // => https://modrinth.com/mod/fabric-language-kotlin/versions
                val ForgeAdapter = "4.3.0" // => https://modrinth.com/mod/kotlin-for-forge/versions
            }

            val Mods = object {
                val Cloth = "12.0.109" // => https://modrinth.com/mod/cloth-config/versions
                val ModMenu = "8.0.0" // => https://modrinth.com/mod/modmenu/versions
            }

            // Versions

            val ejektaGroup = "io.ejekta"
            val modPackage = "bountiful"
            val modVersion = "6.1.0"

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

