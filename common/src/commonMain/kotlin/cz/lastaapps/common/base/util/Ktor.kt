package cz.lastaapps.common.base.util

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.error.Network
import cz.lastaapps.common.base.toResult
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import org.lighthousegames.logging.logging


suspend fun HttpResponse.bodyAsSafeText(): String = String(bodyAsChannel().toByteArray())

suspend fun <R : Any> runCatchingKtor(block: suspend () -> Result<R>) = runCatchingNetworkExceptions { block() }

inline fun <R : Any> runCatchingNetworkExceptions(block: () -> Result<R>): Result<R> =
    runCatching(block).getOrElse { e ->
        when (val name = e::class.simpleName) {
            "UnknownHostException" -> Network.NoNetworkConnection(name, e)
            "NoRouteToHostException",
            "IOException",
            "SSLException",
            "SocketException",
            "HttpRequestTimeoutException",
            "ConnectException",
            "SocketTimeoutException" -> Network.FailedToConnect(name, e)
            else -> {
                val log = logging("CatchingNetwork")
                log.e(e) { "Exception wasn't handled" }
                throw e
            }
        }.toResult()
    }
