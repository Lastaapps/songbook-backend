package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import kotlinx.collections.immutable.ImmutableList

internal interface SearchAuthorDataSource {
    suspend fun searchAuthors(query: String): Result<ImmutableList<Author>>
    suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult>
}
