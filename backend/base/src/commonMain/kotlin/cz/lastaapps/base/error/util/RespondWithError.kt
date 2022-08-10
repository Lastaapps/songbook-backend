package cz.lastaapps.base.error.util

import cz.lastaapps.base.ErrorResult
import cz.lastaapps.base.Result
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

suspend fun <T : Any> ApplicationCall.respondWithError(error: Result.Error<T>) = respondWithError(error.error)

@Serializable
private data class ErrorPayload(
    val name: String,
    val message: String?,
)

suspend fun ApplicationCall.respondWithError(error: ErrorResult) {
    respond(error.httpCode, error.toPayload())
}

private fun ErrorResult.toPayload() = ErrorPayload(this::class.simpleName!!, message)
