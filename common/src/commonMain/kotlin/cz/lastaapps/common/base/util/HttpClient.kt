package cz.lastaapps.common.base.util

import io.ktor.client.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val songBookHttpClient
    get() = HttpClient {
        engine {
            threadsCount = 4
        }
        install(ContentEncoding)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        // TODO implement proper cache
        install(HttpCache)
        expectSuccess = true
    }
