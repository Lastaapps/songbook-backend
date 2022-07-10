package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult

internal interface SearchSongByAuthorDataSource {
    suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult>
}