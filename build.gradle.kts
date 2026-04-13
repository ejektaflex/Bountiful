plugins {
    id("multiloader-root")
}

val bundleMods by tasks.registering(Sync::class) {
    group = "build"
    description = "Builds and collects Fabric and NeoForge jars for Bountiful, Kambrik, and Percale into build/bundle/mods."

    val bountifulVersion = providers.gradleProperty("version")
    val kambrikVersion = providers.gradleProperty("kambrik_version")
    val percaleVersion = providers.gradleProperty("percale_version")

    dependsOn(":fabric:build", ":neoforge:build")
    dependsOn(gradle.includedBuild("Kambrik").task(":fabric:build"))
    dependsOn(gradle.includedBuild("Kambrik").task(":neoforge:build"))

    into(layout.buildDirectory.dir("bundle/mods"))

    from(layout.projectDirectory.file("fabric/build/libs/bountiful-fabric-${bountifulVersion.get()}.jar"))
    from(layout.projectDirectory.file("neoforge/build/libs/bountiful-neoforge-${bountifulVersion.get()}.jar"))
    from(layout.projectDirectory.file("../Kambrik/fabric/build/libs/kambrik-fabric-${kambrikVersion.get()}.jar"))
    from(layout.projectDirectory.file("../Kambrik/neoforge/build/libs/kambrik-neoforge-${kambrikVersion.get()}.jar"))

    // Percale is optional here: include existing jars without forcing its build to succeed.
    from(layout.projectDirectory.file("../Percale/fabric/build/libs/percale-fabric-${percaleVersion.get()}.jar"))
    from(layout.projectDirectory.file("../Percale/neoforge/build/libs/percale-neoforge-${percaleVersion.get()}.jar"))
}

tasks.register("bundle") {
    group = "build"
    description = "Alias for bundleMods."
    dependsOn(bundleMods)
}
