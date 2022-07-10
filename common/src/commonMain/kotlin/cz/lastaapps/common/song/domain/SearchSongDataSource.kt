package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult

internal interface SearchSongDataSource {
    suspend fun searchByName(query: String): Result<OnlineSearchResult>
    suspend fun searchByText(query: String): Result<OnlineSearchResult>
}
