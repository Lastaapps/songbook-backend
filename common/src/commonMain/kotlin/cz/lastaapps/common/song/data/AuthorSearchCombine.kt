package cz.lastaapps.common.song.data

import cz.lastaapps.common.base.*
import cz.lastaapps.common.song.domain.SearchAuthorDataSource
import cz.lastaapps.common.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType

internal class AuthorSearchCombine(private val authorSource: SearchAuthorDataSource) : SearchSongByAuthorDataSource {
    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val interpreters = authorSource.searchAuthors(query)
        if (interpreters.isError()) return interpreters.casted()

        val songs = interpreters.asSuccess().data.filter { it.songNumber != 0 }.map {
            val res = authorSource.loadSongsForAuthor(it)
            if (res.isError()) return res.casted()
            res.asSuccess().data
        }.flatten()

        return OnlineSearchResult(OnlineSource.SuperMusicSk, setOf(SearchType.AUTHOR), songs).toSuccess()
    }
}