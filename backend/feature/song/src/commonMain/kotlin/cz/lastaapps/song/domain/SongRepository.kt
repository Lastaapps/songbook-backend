package cz.lastaapps.song.domain

import cz.lastaapps.base.Result
import cz.lastaapps.song.domain.model.search.SearchType
import cz.lastaapps.song.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList

internal interface SongRepository {
    suspend fun search(
        query: String,
        searchType: SearchType,
    ): Result<ImmutableList<SearchedSong>>
}