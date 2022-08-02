package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.search.OnlineSearchResult

internal interface SearchSongByNameDataSource {
    suspend fun searchByName(query: String): Result<OnlineSearchResult>
}
