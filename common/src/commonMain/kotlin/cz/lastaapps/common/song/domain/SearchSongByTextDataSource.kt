package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult

internal interface SearchSongByTextDataSource {
    suspend fun searchByText(query: String): Result<OnlineSearchResult>
}
