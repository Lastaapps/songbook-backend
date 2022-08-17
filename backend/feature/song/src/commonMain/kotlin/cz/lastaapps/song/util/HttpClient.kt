package cz.lastaapps.song.util

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val songBookHttpClient
    get() = HttpClient {
        install(ContentEncoding)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpRequestRetry) {
            constantDelay(250)
            retryOnServerErrors(1)
        }
        // TODO implement proper cache
        install(HttpCache)
        expectSuccess = true
    }
