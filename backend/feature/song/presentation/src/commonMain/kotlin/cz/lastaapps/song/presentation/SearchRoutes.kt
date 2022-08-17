package cz.lastaapps.song.presentation

import cz.lastaapps.base.Result
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.error.util.respondWithError
import cz.lastaapps.base.util.params
import cz.lastaapps.song.domain.usecase.LoadSongUseCase
import cz.lastaapps.song.domain.usecase.SearchSongUseCase
import cz.lastaapps.song.presentation.model.Source
import cz.lastaapps.song.presentation.model.payload.toPayload
import cz.lastaapps.song.presentation.model.toDomain
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class SearchRoutes(
    app: Application,
    private val searchSong: SearchSongUseCase,
    private val loadSong: LoadSongUseCase,
) {
    init {
        app.routing {
            authenticate {
                route("api/search") {
                    query()
                    load()
                }
            }
        }
    }

    private fun Route.query() {
        get("query") {
            val query = params["query"]
            val page = params["page"]?.toIntOrNull()
            val name = params["byName"]?.toBooleanStrictOrNull() ?: false
            val author = params["byAuthor"]?.toBooleanStrictOrNull() ?: false
            val text = params["byText"]?.toBooleanStrictOrNull() ?: false

            if (query.isNullOrBlank()) {
                call.respondWithError(SongErrors.WebError.QueryMissing)
                return@get
            }
            if (query.trim().length <= 2) {
                call.respondWithError(SongErrors.WebError.QueryToShort)
                return@get
            }
            if (page?.let { it < 0 } == true) {
                call.respondWithError(SongErrors.WebError.NegativePage)
                return@get
            }
            if (!name && !author && !text) {
                call.respondWithError(SongErrors.WebError.NoQueryType)
                return@get
            }
            val params = SearchSongUseCase.Params(query, name, text, author, page)

            when (val res = searchSong(params)) {
                is Result.Error -> call.respondWithError(res.error)
                is Result.Success -> call.respond(res.data.map { it.toPayload() })
            }
        }
    }

    private fun Route.load() {
        get("load") {
            val source = params["source"]?.let {
                Source.from(it) ?: run {
                    call.respondWithError(SongErrors.WebError.UnknownSource(it))
                    return@get
                }
            }?.toDomain() ?: run {
                call.respondWithError(SongErrors.WebError.MissionSource)
                return@get
            }

            val remoteId = params["remoteId"]?.takeIf { it.isNotBlank() } ?: run {
                call.respondWithError(SongErrors.WebError.MissionId)
                return@get
            }
            val params = LoadSongUseCase.Params(source, remoteId)

            when (val res = loadSong(params)) {
                is Result.Error -> call.respondWithError(res)
                is Result.Success -> call.respond(res.data.toPayload())
            }
        }
    }
}
