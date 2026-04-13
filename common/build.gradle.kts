plugins {
    id("multiloader-common-module")
}

sourceSets {
    create("gametest") {
        java.srcDir("src/gametest/java")
        resources.srcDir("src/gametest/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["main"].compileClasspath + sourceSets["main"].runtimeClasspath + configurations["testCompileClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["main"].runtimeClasspath
    }
}

configurations {
    named("gametestCompileOnly") {
        extendsFrom(configurations["compileOnly"])
    }
    named("gametestImplementation") {
        extendsFrom(configurations["implementation"])
        extendsFrom(configurations["testImplementation"])
    }
    named("gametestRuntimeOnly") {
        extendsFrom(configurations["runtimeOnly"])
        extendsFrom(configurations["testRuntimeOnly"])
    }
}

repositories {
    maven {
        name = "Architectury Maven"
        url = uri("https://maven.architectury.dev/")
    }
    mavenLocal()
}

dependencies {
    implementation("io.ejekta.kambrik:kambrik-common-${project.property("minecraft_version")}:${project.property("kambrik_version")}")
    implementation("io.ejekta.percale:percale-common-${project.property("minecraft_version")}:${project.property("percale_version")}")
    testImplementation(kotlin("test"))
}
