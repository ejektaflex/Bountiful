
object Versions {
    val MC = "1.19.4"
}

architectury {
    // Create the IDE launch configurations for this subproject.
    platformSetupLoomIde()
    // Set up Architectury for Forge.
    forge()
}

loom {
    forge {
        mixinConfig("bountiful.mixins.json")
        accessWidenerPath.set(project(":common").file("src/main/resources/bountiful.accesswidener"))
        convertAccessWideners.set(true)
    }
}

repositories {
    // Set up Kotlin for Forge's Maven repository.
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven("https://maven.shedaniel.me/") // Cloth Config
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Add dependency on Forge. This is mainly used for generating the patched Minecraft jar with Forge classes.
    forge("net.minecraftforge:forge:${Versions.MC}-45.0.20")

    // Add Kotlin for Forge.
    // Based on their own instructions: https://github.com/thedarkcolour/KotlinForForge/blob/70385f5/thedarkcolour/kotlinforforge/gradle/kff-3.0.0.gradle
    implementation("thedarkcolour:kotlinforforge:4.1.0")

    modImplementation("io.ejekta:kambrik-forge:6.0.0-beta.2+1.19.4") {
        isTransitive = false
    }

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    // Bundle the transformed version of the common project in the mod.
    // The transformed version includes things like fixed refmaps.
    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    modImplementation("me.shedaniel.cloth:cloth-config-forge:10.0.96")
}

tasks {

    processResources {
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", rootProject.version.toString())

        // Replace the $version template in mods.toml with the project version.
        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version.toString())
        }
    }
}
kotlin {
    jvmToolchain(17)
}
