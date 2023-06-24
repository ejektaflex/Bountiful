import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.ksx
    base
    // https://maven.architectury.dev/architectury-plugin/architectury-plugin.gradle.plugin/
    id("architectury-plugin") version libs.versions.architectury
    // https://maven.architectury.dev/dev/architectury/loom/dev.architectury.loom.gradle.plugin/
    id("dev.architectury.loom") version libs.versions.archloom apply false
    id("com.github.johnrengelman.shadow") version libs.versions.shadow apply false
}

// Set the Minecraft version for Architectury.
architectury {
    minecraft = libs.versions.mc.get()
}

group = libs.versions.pkg.get()
version = libs.versions.fullversion.get()
base.archivesName.set("Bountiful")

tasks {
    // Register a custom "collect jars" task that copies the Fabric and Forge mod jars into the root project's build/libs.
    val collectJars by registering(Copy::class) {
        // Find the remapJar tasks of projects that aren't :common (so :fabric and :forge) and depend on them.
        val tasks = subprojects.filter { it.path != ":common" }.map { it.tasks.named("remapJar") }
        dependsOn(tasks)

        // Copy the outputs of the tasks...
        from(tasks)
        // ...into build/libs.
        into(buildDir.resolve("libs"))
    }

    // Set up assemble to depend on the collectJars task, so it gets run on gradlew build.
    assemble {
        dependsOn(collectJars)
    }
}

// Do the shared setup for the Minecraft subprojects.
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    group = rootProject.group
    version = rootProject.version
    base.archivesName.set(rootProject.base.archivesName)

    dependencies {
        // Note that the configuration name has to be in quotes (a string) since Loom isn't applied to the root project,
        // and so the Kotlin accessor method for it isn't generated for this file.
        "minecraft"(rootProject.libs.minecraft)
        implementation(rootProject.libs.ksx)
        "mappings"("net.fabricmc:yarn:${rootProject.libs.versions.yarn.get()}:v2")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs = listOf("-Xlambdas=indy", "-Xjvm-default=all",)
        }
    }
}


// Set up "platform" subprojects (non-common subprojects).
subprojects {
    if (path != ":common") {

        // Apply the shadow plugin which lets us include contents
        // of any libraries in our mod jars. Architectury uses it
        // for bundling the common mod code in the platform jars.
        apply(plugin = "com.github.johnrengelman.shadow")

        // Define the "bundle" configuration which will be included in the shadow jar.
        val shadowCommon by configurations.creating {
            // This configuration is only meant to be resolved to its files but not published in
            // any way, so we set canBeConsumed = false and canBeResolved = true.
            // See https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:resolvable-consumable-configs.
//            isCanBeConsumed = false
//            isCanBeResolved = true
            configurations.implementation.get().extendsFrom(this)
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }

        tasks {
            "shadowJar"(ShadowJar::class) {
                archiveClassifier.set("dev-shadow")
                if (path == ":forge") { exclude("fabric.mod.json") }
                exclude("architectury.common.json")
                configurations = listOf(shadowCommon)
            }
            "remapJar"(RemapJarTask::class) {
                injectAccessWidener.set(true)
                inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
                dependsOn("shadowJar")
                archiveClassifier.set(project.name)
            }
            "jar"(Jar::class) { archiveClassifier.set("dev") }
        }
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenLocal()
    mavenCentral()
}
kotlin {
    jvmToolchain(8)
}