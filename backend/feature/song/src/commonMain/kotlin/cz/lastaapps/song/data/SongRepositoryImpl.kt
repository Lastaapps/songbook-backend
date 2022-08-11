package cz.lastaapps.song.data

import cz.lastaapps.base.Result
import cz.lastaapps.base.toResult
import cz.lastaapps.song.domain.SongRepository
import cz.lastaapps.song.domain.model.search.SearchType
import cz.lastaapps.song.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class SongRepositoryImpl(
    private val sourceAggregator: SourceAggregator,
) : SongRepository {
    override suspend fun search(
        query: String,
        searchType: SearchType,
    ): Result<ImmutableList<SearchedSong>> = coroutineScope {
        when (searchType) {
            SearchType.NAME -> sourceAggregator.searchByName.map {
                async { it.searchByName(query) }
            }

            SearchType.TEXT -> sourceAggregator.searchByText.map {
                async { it.searchByText(query) }
            }

            SearchType.AUTHOR -> sourceAggregator.searchByAuthor.map {
                async { it.searchSongsByAuthor(query) }
            }
        }
    }.awaitAll().map {
        when (it) {
            is Result.Error -> {
                //TODO report
                emptyList()
            }

            is Result.Success -> it.data
        }
    }.flatten().toImmutableList().toResult()
}
