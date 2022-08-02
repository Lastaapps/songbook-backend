package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.search.OnlineSearchResult

internal interface SearchSongByAuthorDataSource {
    suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult>
}