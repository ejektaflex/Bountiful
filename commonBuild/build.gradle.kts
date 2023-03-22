
plugins {
    id("java")
    `kotlin-dsl`
}

group = "io.ejekta.kambrik"
version = "0.1+1.19.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}

