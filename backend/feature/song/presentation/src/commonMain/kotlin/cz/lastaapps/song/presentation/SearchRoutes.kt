package cz.lastaapps.song.presentation

import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.error.util.respondWithError
import cz.lastaapps.base.util.params
import cz.lastaapps.song.presentation.model.Source
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

class SearchRoutes(
    app: Application,
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
            val name = params["byName"] ?: false
            val author = params["byAuthor"] ?: false
            val text = params["byText"] ?: false

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
            if (name == false && author == false && text == false) {
                call.respondWithError(SongErrors.WebError.NoQueryType)
                return@get
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
            } ?: run {
                call.respondWithError(SongErrors.WebError.MissionSource)
                return@get
            }
            val remoteId = params["remoteId"]?.takeIf { it.isNotBlank() } ?: run {
                call.respondWithError(SongErrors.WebError.MissionId)
                return@get
            }


        }
    }
}
