
architectury {
    // Create the IDE launch configurations for this subproject.
    platformSetupLoomIde()
    // Set up Architectury for Fabric.
    fabric()
}

// The files below are for using the access widener for the common project.
// We need to copy the file from common resources to fabric resource
// for Fabric Loader to find it and not crash.

// The generated resources directory for the AW.
val generatedResources = file("src/generated/resources")
// The path to the AW file in the common subproject.
val accessWidenerFile = project(":common").file("src/main/resources/kambrik.accesswidener")

loom {
    // Make the Fabric project use the common access widener.
    // Technically useless, BUT this file is also needed at dev runtime of course
    accessWidenerPath.set(accessWidenerFile)
}

sourceSets {
    main {
        resources {
            // Mark the AW generated resource directory as
            // a source directory for the resources of
            // the "main" source set.
            srcDir(generatedResources)
        }
    }
}

// Set up various Maven repositories for mod compat.
repositories {

    // TerraformersMC maven for Mod Menu and EMI.
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/releases")

        content {
            includeGroup("com.terraformersmc")
        }
    }

}

dependencies {

    // Depend on the common project. The "namedElements" configuration contains the non-remapped
    // classes and resources of the project.
    // It follows Gradle's own convention of xyzElements for "outgoing" configurations like apiElements.
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    // Bundle the transformed version of the common project in the mod.
    // The transformed version includes things like fixed refmaps.
    bundle(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    // Standard Fabric mod setup.
    modImplementation("net.fabricmc:fabric-loader:0.14.17")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.76.0+1.19.4")
    modApi("net.fabricmc:fabric-language-kotlin:1.9.2+kotlin.1.8.10")

    // Bundle Jankson in the mod and use it as a regular "implementation" library.
    //bundle(implementation("blue.endless:jankson:${rootProject.property("jankson")}")!!)

    // Mod compat
    //modLocalRuntime(modCompileOnly("com.terraformersmc:modmenu:${rootProject.property("modmenu")}")!!)

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
        inputs.property("version", project.version)

        // Replace the $version template in fabric.mod.json with the project version.
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

}
