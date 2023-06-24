plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.6.0"
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
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    modImplementation("io.ejekta:kambrik-common:6.0.1+1.20.1")
    modImplementation("me.shedaniel.cloth:cloth-config:11.0.99")
}

kotlin {
    jvmToolchain(17)
}

