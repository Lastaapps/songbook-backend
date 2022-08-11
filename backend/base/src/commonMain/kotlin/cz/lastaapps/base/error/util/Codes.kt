package cz.lastaapps.base.error.util

import cz.lastaapps.base.ErrorResult
import cz.lastaapps.base.error.SongErrors
import io.ktor.http.*

internal val ErrorResult.httpCode: HttpStatusCode
    get() = when (this) {
        is SongErrors -> httpCode
        else -> error("Unset code for exception: ${this::class.simpleName}")
    }

private val SongErrors.httpCode: HttpStatusCode
    get() = when (this) {
        is SongErrors.InvalidAuthorId,
        is SongErrors.ParseError.FailedToMatchInterpreterList,
        is SongErrors.ParseError.FailedToMatchInterpreterSongList,
        is SongErrors.ParseError.FailedToMatchSongList,
        is SongErrors.ParseError.FailedToMatchSongNameOrAuthor,
        is SongErrors.ParseError.FailedToMatchSongText,
        is SongErrors.ToShortQuery,
        SongErrors.WebError.MissionId,
        SongErrors.WebError.MissionSource,
        SongErrors.WebError.NegativePage,
        SongErrors.WebError.NoQueryType,
        SongErrors.WebError.QueryMissing,
        SongErrors.WebError.QueryToShort,
        is SongErrors.WebError.UnknownSource,
        is SongErrors.ParseError.UnknownParsingError
        -> HttpStatusCode.BadRequest
    }
