plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
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
    modImplementation("net.fabricmc:fabric-loader:0.14.17")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    modImplementation("io.ejekta:kambrik-common:6.0.0-beta.2+1.19.4")
    modImplementation("me.shedaniel.cloth:cloth-config:10.0.96")
}

kotlin {
    jvmToolchain(17)
}

