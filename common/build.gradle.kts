plugins {
    id("multiloader-common-module")
}

repositories {
    maven {
        name = "Architectury Maven"
        url = uri("https://maven.architectury.dev/")
    }
    mavenLocal()
}

dependencies {
    implementation("me.shedaniel.cloth:cloth-config-neoforge:${project.property("cloth_config_version")}")
    implementation("io.ejekta.kambrik:kambrik-common:${project.property("kambrik_version")}") {
        isTransitive = false
    }
    implementation("io.ejekta.percale:percale-neoforge:${project.property("percale_version")}") {
        isTransitive = false
    }
}
