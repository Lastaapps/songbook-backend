package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.ErrorResult

sealed class SongErrors(message: String?, throwable: Throwable?) : ErrorResult(message, throwable) {
    sealed class ParseError(message: String?, throwable: Throwable?) : SongErrors(message, throwable) {
        class FailedToMatchSongList(throwable: Throwable? = null) : ParseError("Failed to match song list", throwable)

        class FailedToMatchInterpreterList(throwable: Throwable? = null) :
            ParseError("Failed to match interpreter list", throwable)

        class FailedToMatchInterpreterSongList(throwable: Throwable? = null) :
            ParseError("Failed to match interpreter song list", throwable)

        class SongCouldNotBeRead(throwable: Throwable? = null) : ParseError("Song could not be read", throwable)

        class ToShortQuery(minLength: Int) : ParseError("Query is to short, server supports $minLength+ chars", null)
    }
}
