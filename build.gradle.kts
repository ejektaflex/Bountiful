import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	//id 'com.github.johnrengelman.shadow' version '6.1.0'
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.serialization") version "1.5.31"
	id("fabric-loom") version "0.10-SNAPSHOT"
}

object Versions {
	const val Minecraft = "1.18-pre2"
	object Jvm {
		val Java = JavaVersion.VERSION_17
		const val Kotlin = "1.6.0"
		const val TargetKotlin = "17"
	}
	object Fabric {
		const val Yarn = "1.18-pre2+build.1"
		const val Loader = "0.12.5"
		const val Api = "0.42.4+1.18"
	}
	object Mod {
		const val Group = "io.ejekta"
		const val ID = "bountiful"
		const val Version = "2.0.1"
	}
	object Env {
		const val Kambrik = "3.+"
		const val FLK = "1.6.5+kotlin.1.5.31"
		const val ClothConfig = "6.0.42"
		const val ModMenu = "2.0.6"
	}
}

val modId: String by project
val modVersion: String by project
val group: String by project
val minecraftVersion: String by project
val fabricVersion: String by project
val kotlinVersion: String by project
val loaderVersion: String by project
val yarnMappings: String by project

project.group = group
version = modVersion



//compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
	mavenLocal()
	mavenCentral()
	maven(url = "https://kotlin.bintray.com/kotlinx")
	maven(url = "https://maven.shedaniel.me/")
	maven(url = "https://maven.terraformersmc.com/") {
		name = "Mod Menu"
	}
}

minecraft { }

dependencies {
	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

	// Kambrik API
	modImplementation("io.ejekta:kambrik:3.+")

	modApi("me.shedaniel.cloth:cloth-config-fabric:6.0.42") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	implementation("com.google.code.findbugs:jsr305:3.0.2")

	modApi("com.terraformersmc:modmenu:2.0.6") {
		exclude(module = "fabric-api")
		exclude(module = "config-2")
	}

	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.6.5+kotlin.1.5.31")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
}


tasks.getByName<ProcessResources>("processResources") {
	filesMatching("fabric.mod.json") {
		expand(
			mutableMapOf<String, String>(
				"modid" to modId,
				"version" to modVersion,
				"kotlinVersion" to kotlinVersion,
				"fabricApiVersion" to fabricVersion
			)
		)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "16"
	}
}
