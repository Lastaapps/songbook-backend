package cz.lastaapps.common.base.util

import io.ktor.client.statement.*
import io.ktor.util.cio.*


suspend fun HttpResponse.bodyAsSafeText(): String = String(bodyAsChannel().toByteArray())
