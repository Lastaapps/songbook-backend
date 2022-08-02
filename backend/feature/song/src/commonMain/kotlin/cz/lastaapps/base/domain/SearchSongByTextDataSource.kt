package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.search.OnlineSearchResult

internal interface SearchSongByTextDataSource {
    suspend fun searchByText(query: String): Result<OnlineSearchResult>
}
