package cz.lastaapps.base.data

import cz.lastaapps.base.*
import cz.lastaapps.base.domain.SearchAuthorDataSource
import cz.lastaapps.base.domain.SearchSongByAuthorDataSource
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class AuthorSearchCombine(private val authorSource: SearchAuthorDataSource) : SearchSongByAuthorDataSource {
    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val interpreters = authorSource.searchAuthors(query).getIfSuccess { return it }

        return coroutineScope {
            val songs = interpreters.filter { it.songNumber != 0 }.map {
                async { authorSource.loadSongsForAuthor(it) }
            }.awaitAll().map { res ->
                if (res.isError()) return@coroutineScope res.casted()
                res.asSuccess().data.results
            }.flatten().toImmutableList()

            OnlineSearchResult(OnlineSource.SuperMusic, SearchType.AUTHOR, songs).toResult()
        }
    }
}