plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
    kotlin("jvm") version "2.0.21"
}

neoForge {
    neoFormVersion = project.property("neo_form_version") as String
    // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.add(at.absolutePath)
    }
    parchment {
        minecraftVersion = project.property("parchment_minecraft") as String
        mappingsVersion = project.property("parchment_version") as String
    }
}

repositories {
    mavenLocal()
}

dependencies {

    compileOnly("org.spongepowered:mixin:0.8.5")
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")

    implementation("io.ejekta.percale:percale-common:${project.property("percale_version")}")

    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets["main"].java.sourceDirectories.singleFile)
    add("commonResources", sourceSets["main"].resources.sourceDirectories.singleFile)
}
