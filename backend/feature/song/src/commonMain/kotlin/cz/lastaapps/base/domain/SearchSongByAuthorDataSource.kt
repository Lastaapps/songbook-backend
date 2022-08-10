package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList

internal interface SearchSongByAuthorDataSource {
    suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>>
}