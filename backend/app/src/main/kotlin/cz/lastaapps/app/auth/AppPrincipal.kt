package cz.lastaapps.app.auth

import io.ktor.server.auth.*

data class AppPrincipal(val key: String) : Principal
