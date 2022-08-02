package cz.lastaapps.base.domain

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
    }

    class ToShortQuery(minLength: Int, throwable: Throwable? = null) :
        SongErrors("Query is to short, server supports $minLength+ chars", throwable)
}
