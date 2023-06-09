
plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

// The generated resources directory for the AW.
val generatedResources = file("src/generated/resources")
// The path to the AW file in the common subproject.
val accessWidenerFile = project(":common").file("src/main/resources/bountiful.accesswidener")

loom { accessWidenerPath.set(accessWidenerFile) }

// Mark the AW generated resource directory as a source directory for the resources of the "main" source set.
sourceSets {
    main {
        resources {
            // TODO does this break AW?
            //srcDir(generatedResources)
            srcDir(project(":common").file("src/main/resources"))
        }
    }
}

// Set up various Maven repositories for mod compat.
repositories {
    maven("https://maven.terraformersmc.com/releases") // Modmenu
    maven("https://maven.terraformersmc.com/releases/") // Shedaniel
    mavenLocal() // Kambrik
}

// Please just use current fab loader
configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:0.14.21")
    }
}

dependencies {
    implementation(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

    // Standard Fabric mod setup.
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.0+1.20")  {
        exclude("net.fabricmc", "fabric-loader")
    }
    modApi("net.fabricmc:fabric-language-kotlin:1.9.4+kotlin.1.8.21")
    modImplementation("io.ejekta:kambrik-fabric:6.0.1+1.20")
    modImplementation("com.terraformersmc:modmenu:7.0.1")
}

tasks {
    // The AW file is needed in :fabric project resources when the game is run.
    // This task simply copies it.
    val copyAccessWidener by registering(Copy::class) {
        from(accessWidenerFile)
        into(generatedResources)
    }

    processResources {
        // Hook the AW copying to processResources.
        dependsOn(copyAccessWidener)
        // Mark that this task depends on the project version,
        // and should reset when the project version changes.
        inputs.property("version", rootProject.version.toString())

        // Replace the $version template in fabric.mod.json with the project version.
        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version.toString())
        }
    }

}
