package cz.lastaapps.app.config

import io.ktor.server.config.*

enum class Environment {
    PRODUCTION, DEVELOPMENT;
}

data class ServerConfig(
    val usesSSL: Boolean,
    val apiKeys: Set<String>,
    val environment: Environment,
) {
    companion object {
        fun from(config: ApplicationConfig) = with(config) {
            ServerConfig(
                property("app.useSSL").getString().toBoolean(),
                property("app.apiKeys").getString()
                    .split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet(),
                property("app.environment").getString().let {
                    when (it) {
                        "prod" -> Environment.PRODUCTION
                        "dev" -> Environment.DEVELOPMENT
                        else -> error("Unsupported env: $it")
                    }
                }
            )
        }
    }
}