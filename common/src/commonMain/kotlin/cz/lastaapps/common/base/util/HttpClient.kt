package cz.lastaapps.common.base.util

import io.ktor.client.*
import io.ktor.client.plugins.compression.*

val songBookHttpClient
    get() = HttpClient {
        install(ContentEncoding)
    }
