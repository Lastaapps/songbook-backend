package cz.lastaapps.song.domain

import cz.lastaapps.base.Result
import cz.lastaapps.song.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList

internal interface SearchSongByNameDataSource {
    suspend fun searchByName(query: String): Result<ImmutableList<SearchedSong>>
}
