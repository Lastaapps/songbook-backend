package cz.lastaapps.app

import cz.lastaapps.app.auth.AppPrincipal
import cz.lastaapps.app.config.ServerConfig
import dev.forst.ktor.apikey.apiKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val app = this
    val config = ServerConfig.from(environment.config)

    install(Koin) {
        modules(
            org.koin.dsl.module {
                single { app }
                single { environment.config }
                single { config }
            },
            appModule,
        )
    }

    install(AutoHeadResponse)

    install(DefaultHeaders)

    install(Authentication) {
        apiKey {
            validate { keyFromHeader ->
                keyFromHeader
                    .takeIf { it in config.apiKeys || config.apiKeys.isEmpty() }
                    ?.let { AppPrincipal(it) }
            }
        }
    }

    install(Compression) {
        gzip {
            priority = 0.9
//            condition {
//                request.headers[HttpHeaders.Referrer]?.startsWith("https://${this@module.environment}/") == true
//                        || request.headers[HttpHeaders.Referrer]?.startsWith("http://") == true
//            }
        }
        deflate {
            priority = 1.0
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
//                is AuthorizationException ->
//                    call.respondText(text = "403: $cause", status = HttpStatusCode.Forbidden)
                else -> call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page Not Found, see project docs", status = status)
        }
    }

    install(ForwardedHeaders)

    if (config.usesSSL) {
        install(HttpsRedirect) {
            sslPort = 8443
            permanentRedirect = true
        }
    }

    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
//            val userAgent = call.request.headers["User-Agent"]
            val route = call.request.uri
            "Status: $status, HTTP method: $httpMethod, Route: $route"
        }
    }
}
