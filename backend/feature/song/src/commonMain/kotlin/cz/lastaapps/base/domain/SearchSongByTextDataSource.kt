package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList

internal interface SearchSongByTextDataSource {
    suspend fun searchByText(query: String): Result<ImmutableList<SearchedSong>>
}
