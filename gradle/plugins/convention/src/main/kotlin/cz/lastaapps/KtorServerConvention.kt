package cz.lastaapps

import cz.lastaapps.extensions.Constants.IMPL
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KtorServerConvention : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply {
            apply("org.gradle.application")
            alias(libs.plugins.kotlin.jvm)
            alias(libs.plugins.kotlin.serialization)
            alias(libs.plugins.java.kotlin)
            alias(libs.plugins.shadow)
        }

        dependencies.apply {
            add(IMPL, libs.koin.core)
            add(IMPL, libs.koin.ktorServer)

            add(IMPL, libs.ktor.server.core)
            add(IMPL, libs.ktor.server.cio)
            add(IMPL, libs.ktor.server.auth)
            add(IMPL, libs.ktor.server.autoHeadResponse)
            add(IMPL, libs.ktor.server.callLogging)
            add(IMPL, libs.ktor.server.compression)
            add(IMPL, libs.ktor.server.contentNegotiation)
            add(IMPL, libs.ktor.server.defaultHeaders)
            add(IMPL, libs.ktor.server.forwardedHeaders)
            add(IMPL, libs.ktor.server.httpRedirect)
            add(IMPL, libs.ktor.server.jsonSerialization)
            add(IMPL, libs.ktor.server.statusPages)
            add(IMPL, libs.ktor.server.sesstions)
            add(IMPL, libs.ktor.server.unofficial.apiKeyAuth)
        }
    }
}