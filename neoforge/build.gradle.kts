
architectury {
    // Create the IDE launch configurations for this subproject.
    platformSetupLoomIde()
    // Set up Architectury for Forge.
    neoForge()
}

loom {
    neoForge {
        accessWidenerPath.set(project(":common").file("src/main/resources/bountiful.accesswidener"))
        //convertAccessWideners.set(true)
    }
}

repositories {
    // Set up Kotlin for Forge's Maven repository.
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    maven("https://maven.shedaniel.me/") // Cloth Config
    maven("https://maven.neoforged.net/releases/") // NeoForge
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Add dependency on Forge. This is mainly used for generating the patched Minecraft jar with Forge classes.
    neoForge(libs.neoforge)

    // Add Kotlin for Forge.
    // Based on their own instructions: https://github.com/thedarkcolour/KotlinForForge/blob/70385f5/thedarkcolour/kotlinforforge/gradle/kff-3.0.0.gradle
    implementation(libs.forge.adapter)

    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    // Bundle the transformed version of the common project in the mod.
    // The transformed version includes things like fixed refmaps.
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) { isTransitive = false }

    //implementation("org.ow2.asm:asm-tree:9.4")

    //modImplementation(libs.mod.dep.cloth.config.forge)
    modImplementation(libs.kambrik.forge) {
        isTransitive = false
    }
}

tasks {

    // Remaps AW to AT
    remapJar {
        atAccessWideners.add("bountiful.accesswidener")
    }

    processResources {
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", libs.versions.mod.get())

        // Replace the $version template in mods.toml with the project version.
        filesMatching("META-INF/mods.toml") {
            expand(mapOf(
                "version" to libs.versions.mod.get(),
                "kambrik_version" to libs.versions.kambrik.get(),
                "minecraft_version" to libs.versions.mc.get()
            ))
        }
    }
}
kotlin {
    jvmToolchain(17)
}
