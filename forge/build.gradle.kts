
object Versions {
    val Mod = "0.1"
    val MC = "1.19.4"
    val Yarn = "1.19.4+build.1"
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
        //convertAccessWideners.set(true)
    }
}

repositories {
    // Set up Kotlin for Forge's Maven repository.
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Add dependency on Forge. This is mainly used for generating the patched Minecraft jar with Forge classes.
    forge("net.minecraftforge:forge:${Versions.MC}-45.0.20")

    // Add Kotlin for Forge.
    // Based on their own instructions: https://github.com/thedarkcolour/KotlinForForge/blob/70385f5/thedarkcolour/kotlinforforge/gradle/kff-3.0.0.gradle
    implementation("thedarkcolour:kotlinforforge:4.1.0")

    modImplementation("io.ejekta:kambrik-forge:123-SNAPSHOT.+") {
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
}

tasks {

    processResources {
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", project.version)

        // Replace the $version template in mods.toml with the project version.
        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }
}
kotlin {
    jvmToolchain(17)
}
