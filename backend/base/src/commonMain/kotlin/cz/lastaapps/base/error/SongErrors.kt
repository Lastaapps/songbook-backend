package cz.lastaapps.base.error

import cz.lastaapps.base.ErrorResult

sealed class SongErrors(message: String?, throwable: Throwable?) : ErrorResult(message, throwable) {
    sealed class ParseError(message: String?, throwable: Throwable?) : SongErrors(message, throwable) {

        class FailedToMatchSongList(throwable: Throwable? = null) :
            ParseError("Failed to match song list", throwable)

        class FailedToMatchInterpreterList(throwable: Throwable? = null) :
            ParseError("Failed to match interpreter list", throwable)

        class FailedToMatchInterpreterSongList(throwable: Throwable? = null) :
            ParseError("Failed to match interpreter song list", throwable)

        class FailedToMatchSongText(throwable: Throwable? = null) :
            ParseError("Failed to match song text", throwable)

        class FailedToMatchSongNameOrAuthor(throwable: Throwable? = null) :
            ParseError("Failed to match song name or author", throwable)
    }

    class ToShortQuery(minLength: Int, throwable: Throwable? = null) :
        SongErrors("Query is to short, server supports $minLength+ chars", throwable)

    class InvalidAuthorId(id: String) : SongErrors("Cannot find author specified by id $id", null)

    sealed class WebError(message: String?, throwable: Throwable? = null) : SongErrors(message, throwable) {
        // search
        object QueryMissing : WebError("Query is missing")
        object QueryToShort : WebError("Query must contain at least 3 characters")
        object NegativePage : WebError("Negative page number is not supported")
        object NoQueryType : WebError("You have to specify a query type")

        // load
        object MissionSource : WebError("source params is missing")
        class UnknownSource(name: String) : WebError("Unknown source: $name")
        object MissionId : WebError("remoteId param is missing")
    }
}
