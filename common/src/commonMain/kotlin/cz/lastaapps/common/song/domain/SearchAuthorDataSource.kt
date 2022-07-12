package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult

internal interface SearchAuthorDataSource {
    suspend fun searchAuthors(query: String): Result<List<Author>>
    suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult>
}
