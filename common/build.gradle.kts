plugins {
    id("org.jetbrains.kotlin.jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.ksx
}

architectury { common("fabric", "forge") }

loom { accessWidenerPath.set(file("src/main/resources/bountiful.accesswidener")) }

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.terraformersmc.com/releases/") // Shedaniel
}

dependencies {
    // Add dependencies on the required Kotlin modules.
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    modImplementation(libs.bundles.mod.deps.common)
}

kotlin {
    jvmToolchain(17)
}

