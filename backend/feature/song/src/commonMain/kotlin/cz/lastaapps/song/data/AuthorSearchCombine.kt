package cz.lastaapps.song.data

import cz.lastaapps.base.*
import cz.lastaapps.song.domain.SearchAuthorDataSource
import cz.lastaapps.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.song.domain.model.search.SearchedSong
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class AuthorSearchCombine(private val authorSource: SearchAuthorDataSource) : SearchSongByAuthorDataSource {
    override suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>> {
        val interpreters = authorSource.searchAuthors(query).getIfSuccess { return it }

        return coroutineScope {
            interpreters.filter { it.songNumber != 0 }.map {
                async { authorSource.loadSongsForAuthor(it) }
            }.awaitAll()
        }.map { res ->
            if (res.isError()) return res.casted()
            res.asSuccess().data
        }.flatten().toImmutableList().toResult()
    }
}