//import org.jetbrains.dokka.gradle.DokkaTask
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val v_forgelin = "1.7.3"
val v_jei = "4.11.0.206"
val v_minecraft_root = "1.12"
val v_minecraft = "$v_minecraft_root.2"

buildscript {
    repositories {
        jcenter()
        maven("http://files.minecraftforge.net/maven")
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    }
}

val bouVersion: String
    get() {
        val versionFile = File("src/main/kotlin/bountiful/BountifulInfo.kt")
        val gotVersion = if (versionFile.exists()) {
            versionFile.readLines().find { "VERSION" in it }?.dropWhile { it != '"' }?.drop(1)?.dropLast(1) ?: "UNDEFINED"
        } else {
            "UNDEFINED"
        }
        println("gradle.txt.kts got Bountiful version: $gotVersion")
        return gotVersion
    }

version = bouVersion

base {
    archivesBaseName = "Bountiful"
}

plugins {
    java
    idea
    kotlin("jvm") version "1.3.10"
    id("net.minecraftforge.gradle.forge") version "2.0.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

minecraft {
    version = "1.12.2-14.23.5.2796"
    runDir = "run"
    mappings = "snapshot_20171003"
}

repositories {
    jcenter()
    // Forgelin
    maven("http://maven.shadowfacts.net/")
    // Maven for JEI
    maven("http://dvs1.progwml6.com/files/maven")
    // CurseForge Maven
    maven("https://minecraft.curseforge.com/api/maven/")
    // MMD Maven
    maven("https://maven.mcmoddev.com/")
}

dependencies {
    subprojects.forEach { compile(it) }
    compile("net.shadowfacts:Forgelin:$v_forgelin")
    deobfProvided("mezz.jei:jei_$v_minecraft:$v_jei:api")
    runtime("mezz.jei:jei_$v_minecraft:$v_jei")
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ProcessResources> {
    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("version" to project.version, "mcversion" to project.minecraft.version))
    }
    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
}


tasks.withType<Jar> {
    //from(sourceSets["main"].output)
    from(sourceSets["api"].output.classesDirs)
}

tasks.register<Jar>("apiJar") {
    classifier = "api"
    from(sourceSets["api"].output)
    from(sourceSets["api"].allJava)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}