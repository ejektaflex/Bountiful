package io.ejekta.kambrikdeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope

object KambrikDeps {
    object Fabric {
        val Loader = "net.fabricmc:fabric-loader:0.14.17"
        val API = "net.fabricmc.fabric-api:fabric-api:0.76.0+1.19.4"
    }

}