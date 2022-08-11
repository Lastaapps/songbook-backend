package cz.lastaapps.song.util

import cz.lastaapps.base.Result
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.toResult

inline fun <T : Any?, R : Any> T.runCatchingParse(block: T.() -> Result<R>): Result<R> =
    runCatching(block).getOrElse { e ->
        SongErrors.ParseError.UnknownParsingError(e).toResult()
    }
